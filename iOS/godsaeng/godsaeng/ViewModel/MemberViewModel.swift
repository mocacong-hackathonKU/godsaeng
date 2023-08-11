//
//  MemberViewModel.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/10.

import Foundation
import Combine
import AuthenticationServices
import CryptoKit

// 로그인 관련 비즈니스 로직을 처리하는 뷰모델
class MemberViewModel: NSObject, ObservableObject {
    
    let nonce = Bundle.main.object(forInfoDictionaryKey: "NONCE") as? String ?? ""
    
    var appleLoginInfo = AppleLoginInfo()
    @Published var member: Member = Member()
    @Published var memberId: Int?
    @Published var isRegistered: Bool?
    @Published var error: Error?
    @Published var isDuplicated: Bool?
    
    @Published var memberDataFetched: Bool = false
    
    var cancellables = Set<AnyCancellable>()

    func encdoeNonceSha256(_ input: String) -> String {
        let inputData = Data(input.utf8)
        let hashedData = SHA256.hash(data: inputData)
        let hashString = hashedData.compactMap {
            return String(format: "%02x", $0)
        }.joined()
        return hashString
    }
    
    func loginApple() {
        print("loginApple 함수 호출")
        // 애플 로그인 요청 시 사용되는 요청 객체 생성
        let request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = [.fullName, .email]
        request.nonce = encdoeNonceSha256(nonce)
        // 인증 요청 컨트롤러 생성
        let controller = ASAuthorizationController(authorizationRequests: [request])
        controller.delegate = self
        controller.presentationContextProvider = self
        controller.performRequests()
    }
    
    func insertAppleIdTokenToAppleLoginModel(appleIdToken: String) {
        appleLoginInfo.token = appleIdToken
        if appleLoginInfo.token != nil {
            print("애플 토큰 : ", appleLoginInfo.token)
            requestAppleLoginToServer(appleLoginInfo: appleLoginInfo)
                .sink(receiveCompletion: { result in
                    switch result {
                    case .failure(let error):
                        print("Login error: \(error)")
                    case .finished:
                        break
                    }
                }, receiveValue: { member in
                })
                .store(in: &self.cancellables)
        }
    }
    
    func requestAppleLoginToServer(appleLoginInfo: AppleLoginInfo) -> Future<Member, Error> {
        return Future { promise in
            print("requestURL", requestURL)
            guard let url = URL(string: "\(requestURL)/login/apple") else {
                fatalError("Invalid URL")
            }
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            do {
                let jsonData = try JSONEncoder().encode(appleLoginInfo)
                request.httpBody = jsonData
                request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            } catch {
                promise(.failure(error))
            }
            
            URLSession.shared.dataTaskPublisher(for: request)
                .subscribe(on: DispatchQueue.global(qos: .background))
                .receive(on: DispatchQueue.main)
                .tryMap { data, response -> Data in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    switch httpResponse.statusCode {
                    case 200:
                        print("애플로그인 응답 상태코드 200")
                        AccessManager.shared.isLoggedIn = true
                    case 500 :
                        print("서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("애플로그인 응답 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: Member.self, decoder: JSONDecoder())
                .sink { completion in
                    print("Completion: \(completion)")
                    promise(.success(self.member))
                    AccessManager.shared.isLoggedIn = true
                } receiveValue: { data in
                    self.member = data
                    self.isRegistered = data.isRegistered
                    self.member.platform = "apple"
                    
                    // Save the token using TokenManager
                    if let token = data.token {
                        try? TokenManager.shared.saveToken(token)
                        AccessManager.shared.isLoggedIn = true
                    }
                    print(data.token)
                }
                .store(in: &self.cancellables)
        }
    }

    func checkNicknameDuplicationToServer(nicknameToCheck: String) -> Future<Bool, Error> {
        return Future { promise in
            let targetUrl = "\(requestURL)/members/check-duplicate/nickname?value=\(nicknameToCheck)"
            let encodedUrl = targetUrl.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
            guard let url = URL(string: encodedUrl) else {
                fatalError("Invalid URL")
            }
            
            var request = URLRequest(url: url)
            request.httpMethod = "GET"
            
            print(request)

                        
            URLSession.shared.dataTaskPublisher(for: request)
                .subscribe(on: DispatchQueue.global(qos: .background))
                .receive(on: DispatchQueue.main)
                .tryMap { data, response in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    
                    switch httpResponse.statusCode {
                    case 200:
                        print("닉네임 중복 체크 상태코드 200")
                        self.isDuplicated = true
                    case 500 :
                        print("서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("닉네임 중복 체크 응답 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: Member.self, decoder: JSONDecoder())
                .sink { completion in
                    if let isDuplicated = self.isDuplicated {
                        promise(.success(isDuplicated))
                    }
                    print("Completion: \(completion)")
                } receiveValue: { data in
                    print("닉네임 중복 체크에 대한 응답 데이터 : ", data.result)
                    if data.result == false {
                        self.isDuplicated = false
                    } else {
                        self.isDuplicated = true
                    }
                }
                .store(in: &self.cancellables)
        }
    }

    func requestRegisterToServer(memberToRegister: Member) -> Future<Member, Error> {
        return Future { promise in
            guard let url = URL(string: "\(requestURL)/members/oauth") else {
                fatalError("Invalid URL")
            }
            
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            do {
                let jsonData = try JSONEncoder().encode(memberToRegister)
                request.httpBody = jsonData
                request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            } catch {
            }
            
            URLSession.shared.dataTaskPublisher(for: request)
                .subscribe(on: DispatchQueue.global(qos: .background))
                .receive(on: DispatchQueue.main)
                .tryMap { data, response -> Data in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    switch httpResponse.statusCode {
                    case 200:
                        print("회원가입 상태코드 200")
                        self.isRegistered = true
                    case 500 :
                        print("서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("회원가입 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: Member.self, decoder: JSONDecoder())
                .sink { completion in
                    print("Completion: \(completion)")
                    promise(.success(self.member))
                } receiveValue: { data in
                    self.isRegistered = true
                    self.member.id = data.id
                    self.member.nickname = memberToRegister.nickname
                    print("memberVM의 member의 닉네임 : ", self.member.nickname)
                }
                .store(in: &self.cancellables)
        }
    }
    
    func deleteMember(accessToken: String) {
            guard let url = URL(string: "\(requestURL)/members") else {
                fatalError("Invalid Url")
            }

            var request = URLRequest(url: url)
            request.httpMethod = "DELETE"
            request.addValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
            
        URLSession.shared.dataTaskPublisher(for: request)
                .receive(on: DispatchQueue.main)
                .tryMap { data, response in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    
                    switch httpResponse.statusCode {
                    case 200:
                        print("회원탈퇴 응답 상태코드 200")
                        AccessManager.shared.userLoggedOutOrDeleted = true
                        AccessManager.shared.isLoggedIn = false
                    case 401:
                        print("회원탈퇴 응답 상태코드 401")
                        AccessManager.shared.tokenExpired = true
                        AccessManager.shared.isLoggedIn = false
                    case 500 :
                        print("서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("회원탈퇴 응답 상태코드: \(httpResponse.statusCode)")
                    }
                }
                .sink(receiveCompletion: { completion in
                    switch completion {
                    case .failure(let error):
                        print("회원탈퇴 요청 error: \(error)")
                    case .finished:
                        print("회원탈퇴 요청 finished")
                    }
                }, receiveValue: { _ in
                })
                .store(in: &self.cancellables)
    }
}

// ASAuthorizationControllerDelegate 프로토콜 구현
extension MemberViewModel: ASAuthorizationControllerDelegate {
    
    // 인증이 성공적으로 완료되었을 때 호출되는 콜백 함수
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
            insertAppleIdTokenToAppleLoginModel(appleIdToken: String(decoding: appleIDCredential.identityToken!, as: UTF8.self))
            print("\(appleIDCredential.user)의 인증서발급 성공")
        }
    }
    
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        self.error = error
        print("인증(로그인) 실패 error : \(error)")
    }
}

// ASAuthorizationControllerPresentationContextProviding 구현
extension MemberViewModel: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        // 로그인 화면이 표시될 컨텍스트를 제공
        guard let window = UIApplication.shared.windows.first else {
            fatalError("No window found.")
        }
        return window
    }
}
