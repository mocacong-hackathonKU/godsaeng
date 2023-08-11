//
//  MyViewModel.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/11.
//

import Foundation
import Combine
import SwiftUI

class MyViewModel: ObservableObject {
    
    @Published var myProfileData: Member = Member()
    @Published var profileImageData: Data?
    @Published var profileDataFetched: Bool = false
    
    var cancellables = Set<AnyCancellable>()
    
    func fetchMyProfileData(accessToken: String) {
        requestMyProfileDataFetch(accessToken: accessToken)
            .sink(receiveCompletion: { result in
                switch result {
                case .failure(let error):
                    print("사용자 정보 조회 비동기 처리 error: \(error)")
                case .finished:
                    print("사용자 정보 조회 비동기 처리 종료")
                    self.profileDataFetched = true
                }
            }, receiveValue: { data in
            })
            .store(in: &cancellables)
    }
    
    func requestMyProfileDataFetch(accessToken: String) -> Future<Member, Error> {
        return Future { promise in
            guard let url = URL(string: "\(requestURL)/members/mypage") else {
                fatalError("Invalid URL")
            }
            
            var request = URLRequest(url: url)
            request.httpMethod = "GET"
            request.addValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
            
            print("프로필 조회 요청 : ", request)
            
            URLSession.shared.dataTaskPublisher(for: request)
                .subscribe(on: DispatchQueue.global(qos: .background))
                .receive(on: DispatchQueue.main)
                .tryMap { data, response -> Data in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    switch httpResponse.statusCode {
                    case 200:
                        print("프로필 조회 상태코드 200")
                    case 401:
                        print("프로필 조회 상태코드 401")
                        AccessManager.shared.tokenExpired = true
                        AccessManager.shared.isLoggedIn = false
                    case 500 :
                        print("서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("프로필 조회 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: Member.self, decoder: JSONDecoder())
                .sink { completion in
                    switch completion {
                    case .finished:
                        print("프로필 조회 요청 종료")
                        break
                    case .failure(let error):
                        print("프로필 조회 요청 error : \(error)")
                    }
                } receiveValue: { data in
                    promise(.success(data))
                    self.myProfileData = data
                    if let imgURL = data.imgUrl {
                        self.loadProfileImage(imageUrl: imgURL)
                    } else {
                        self.profileImageData = nil
                    }
                }
                .store(in: &self.cancellables)
        }
    }
    
    func updateProfileImage(accessToken: String, imageData: Data?) {
        requestProfileImageDataUpload(accessToken: accessToken, imageData: imageData)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    print("프로필 이미지 업데이트 비동기 종료")
                case .failure(let error):
                    print("프로필 이미지 업데이트 비동기 error : \(error)")
                }
            }, receiveValue: { _ in
            })
            .store(in: &self.cancellables)
    }
    
    func requestProfileImageDataUpload(accessToken: String, imageData: Data?) -> Future<Bool, Error> {
        return Future { promise in
            guard let url = URL(string: "\(requestURL)/members/mypage/img") else {
                promise(.failure(URLError(.badURL)))
                return
            }
            
            var request = URLRequest(url: url)
            request.httpMethod = "PUT"
            let boundary = UUID().uuidString
            request.addValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
            request.addValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
            
            let data = self.createBody(with: ["file": imageData], boundary: boundary)
            request.httpBody = data
            
            URLSession.shared.dataTask(with: request) { (data, response, error) in
                DispatchQueue.main.async {
                    if let error = error {
                        promise(.failure(error))
                        print("프로필이미지 업데이트 요청 error : \(error)")
                    } else if let httpResponse = response as? HTTPURLResponse {
                        if httpResponse.statusCode == 200 {
                            promise(.success(true))
                            print("프로필이미지 업데이트 응답 상태코드 : ", httpResponse.statusCode)
                        } else if httpResponse.statusCode == 401 {
                            AccessManager.shared.tokenExpired = true
                            AccessManager.shared.isLoggedIn = false
                        } else if httpResponse.statusCode == 500 {
                            AccessManager.shared.serverDown = true
                            AccessManager.shared.isLoggedIn = false
                    }else {
                            promise(.failure(URLError(URLError.Code.badServerResponse)))
                            print("프로필이미지 업데이트 응답 상태코드 : ", httpResponse.statusCode)
                        }
                    }
                }
            }.resume()
        }
    }
    
    private func createBody(with parameters: [String: Any], boundary: String) -> Data {
        
        var body = Data()
        
        for (key, value) in parameters {
            if key == "file" {
                if let image = value as? Data {
                    body.append(Data("--\(boundary)\r\n".utf8))
                    body.append(Data("Content-Disposition: form-data; name=\"\(key)\"; filename=\"image.jpg\"\r\n".utf8))
                    body.append(Data("Content-Type: image/jpeg\r\n\r\n".utf8))
                    body.append(image)
                    body.append(Data("\r\n".utf8))
                } else {
                    print("no image data value")
                }
            }
        }
        body.append(Data("--\(boundary)--\r\n".utf8))
        return body
    }
    
    func loadProfileImage(imageUrl: String) {
        guard let url = URL(string: imageUrl) else {
            print("Invalid URL.")
            return
        }
        loadProfileImageData(url: url)
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { completion in
                    switch completion {
                    case .finished:
                        print("프로필 이미지 다운로드 비동기 종료")
                        break
                    case .failure(let error):
                        print("Failed to load image data: \(error)")
                    }
                },
                receiveValue: { [weak self] data in
                    print("프로필 이미지 다운로드 응답 데이터 :", data)
                    self?.profileImageData = data
                }
            )
            .store(in: &cancellables)
    }
    
    func loadProfileImageData(url: URL) -> AnyPublisher<Data, Error> {
        URLSession.shared.dataTaskPublisher(for: url)
            .tryMap { data, response -> Data in
                guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
                    throw URLError(.badServerResponse)
                }
                return data
            }
            .eraseToAnyPublisher()
    }
}
