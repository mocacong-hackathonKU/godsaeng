//
//  GodsaengViewModel.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/11.
//

import Foundation
import Combine

class GodSaengViewModel: ObservableObject {
    
    @Published var godsaengList: [Godsaeng] = []
    @Published var monthlyGodsaengList: [Godsaeng] = []
    @Published var dailyGodsaengList: [Godsaeng] = []
    @Published var godsaeng: Godsaeng = Godsaeng()
    
    var cancellables = Set<AnyCancellable>()
    
    //같생 작성
    func creatGodsaeng(accessToken: String, godsaengToCreate: Godsaeng) {
        requestgodsaengCreation(accessToken: accessToken, godsaengToCreate: godsaengToCreate)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    print("같생 생성 비동기 성공")
                    
                case .failure(let error):
                    print("같생 생성 비동기 에러 : \(error)")
                }
            }, receiveValue: { godsaengData in
                self.godsaeng.id = godsaengData.id
            })
            .store(in: &self.cancellables)
    }
    func requestgodsaengCreation(accessToken: String, godsaengToCreate: Godsaeng) -> Future<Godsaeng, Error> {
        return Future { promise in
            guard let url = URL(string: "\(requestURL)/godsaeng") else {
                fatalError("Invalid URL")
            }
            
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            do {
                let jsonData = try JSONEncoder().encode(godsaengToCreate)
                request.httpBody = jsonData
                request.addValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
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
                        print("같생 생성 상태코드 200")
                    case 401:
                        print("같생 생성 상태코드 401")
                        AccessManager.shared.tokenExpired = true
                        AccessManager.shared.isLoggedIn = false
                    case 500 :
                        print("서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("같생 생성 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: Godsaeng.self, decoder: JSONDecoder())
                .sink { completion in
                    switch completion {
                    case .finished:
                        break
                    case .failure(let error):
                        print("같생 생성 요청 error : \(error)")
                    }
                } receiveValue: { data in
                    promise(.success(data))
                    self.fetchGodsaengListData(accessToken: accessToken)
                }
                .store(in: &self.cancellables)
        }
    }
    
    //같생 전체 조회
    func fetchGodsaengListData(accessToken: String) {
        requestGodsaengListFetch(accessToken: accessToken)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    print("같생 전체 조회 비동기 성공")
                case .failure(let error):
                    print("같생 전체 조회 비동기 error : \(error)")
                }
            }, receiveValue: { godsaengListdata in
                self.godsaengList = godsaengListdata
            })
            .store(in: &self.cancellables)
    }
    func requestGodsaengListFetch(accessToken: String) -> Future<[Godsaeng], Error> {
        return Future { promise in
            guard let url = URL(string: "\(requestURL)/godsaeng") else {
                fatalError("같생 전체 조회 Invalid URL")
            }
            var request = URLRequest(url: url)
            request.httpMethod = "GET"
            request.addValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
                        
            URLSession.shared.dataTaskPublisher(for: request)
                .subscribe(on: DispatchQueue.global(qos: .background))
                .receive(on: DispatchQueue.main)
                .tryMap { data, response -> Data in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    switch httpResponse.statusCode {
                    case 200:
                        print("같생 전체 조회 상태코드 200")
                    case 401:
                        print("같생 전체 조회 상태코드 401")
                        AccessManager.shared.tokenExpired = true
                        AccessManager.shared.isLoggedIn = false
                    case 500 :
                        print("같생 전체 조회 서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("같생 전체 조회 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: [Godsaeng].self, decoder: JSONDecoder())
                .sink(receiveCompletion: { completion in
                    switch completion {
                    case .failure(let error):
                        promise(.failure( error))
                    case .finished:
                        print("같생 조회 요청 종료")
                        break
                    }
                }, receiveValue: { data in
                    promise(.success(data))
                })
                .store(in: &self.cancellables)
        }
    }
    
    //같생 월별 조회
    func fetchMonthlyGodsaengListData(accessToken: String, currentMonth: String) {
        requestMonthlyGodsaengFetch(accessToken: accessToken, currentMonth: currentMonth)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    print("같생 조회 비동기 성공")
                case .failure(let error):
                    print("같생 조회 비동기 error : \(error)")
                }
            }, receiveValue: { monthlyGodsaengListData in
                self.monthlyGodsaengList = monthlyGodsaengListData
            })
            .store(in: &self.cancellables)
    }
    func requestMonthlyGodsaengFetch(accessToken: String, currentMonth: String) -> Future<[Godsaeng], Error> {
        return Future { promise in
            guard let url = URL(string: "\(requestURL)/godsaeng?date=\(currentMonth)") else {
                fatalError("같생 월별 조회 Invalid URL")
            }
            var request = URLRequest(url: url)
            request.httpMethod = "GET"
            request.addValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
                        
            URLSession.shared.dataTaskPublisher(for: request)
                .subscribe(on: DispatchQueue.global(qos: .background))
                .receive(on: DispatchQueue.main)
                .tryMap { data, response -> Data in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    switch httpResponse.statusCode {
                    case 200:
                        print("같생 월별 조회 상태코드 200")
                    case 401:
                        print("같생 월별 조회 상태코드 401")
                        AccessManager.shared.tokenExpired = true
                        AccessManager.shared.isLoggedIn = false
                    case 500 :
                        print("같생 월별 조회 서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("같생 월별 조회 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: [Godsaeng].self, decoder: JSONDecoder())
                .sink(receiveCompletion: { completion in
                    switch completion {
                    case .failure(let error):
                        promise(.failure( error))
                    case .finished:
                        print("같생 조회 요청 종료")
                        break
                    }
                }, receiveValue: { data in
                    promise(.success(data))
                })
                .store(in: &self.cancellables)
        }
    }
    
    //같생 일별 조회
    func fetchDailyGodsaengListData(accessToken: String, currentDate: String) {
        requestDailyGodsaengFetch(accessToken: accessToken, currentDate: currentDate)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    print("같생 일별 조회 비동기 성공")
                case .failure(let error):
                    print("같생 일별 조회 비동기 error : \(error)")
                }
            }, receiveValue: { dailyGodsaengListData in
                self.dailyGodsaengList = dailyGodsaengListData
            })
            .store(in: &self.cancellables)
    }
    func requestDailyGodsaengFetch(accessToken: String, currentDate: String) -> Future<[Godsaeng], Error> {
        return Future { promise in
            guard let url = URL(string: "\(requestURL)/godsaeng?date=\(currentDate)") else {
                fatalError("같생 일별 조회 Invalid URL")
            }
            var request = URLRequest(url: url)
            request.httpMethod = "GET"
            request.addValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
                        
            URLSession.shared.dataTaskPublisher(for: request)
                .subscribe(on: DispatchQueue.global(qos: .background))
                .receive(on: DispatchQueue.main)
                .tryMap { data, response -> Data in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    switch httpResponse.statusCode {
                    case 200:
                        print("같생 일별 조회 상태코드 200")
                    case 401:
                        print("같생 일별 조회 상태코드 401")
                        AccessManager.shared.tokenExpired = true
                        AccessManager.shared.isLoggedIn = false
                    case 500 :
                        print("같생 일별 조회 서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("같생 일별 조회 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: [Godsaeng].self, decoder: JSONDecoder())
                .sink(receiveCompletion: { completion in
                    switch completion {
                    case .failure(let error):
                        promise(.failure( error))
                    case .finished:
                        print("같생 조회 요청 종료")
                        break
                    }
                }, receiveValue: { data in
                    promise(.success(data))
                })
                .store(in: &self.cancellables)
        }
    }
    
    //같생 상세 조회
    func fetchGodsaengDetailData(accessToken: String, godsaengId: Int) {
        requestGodsaengDetailFetch(accessToken: accessToken, godsaengId: godsaengId)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .finished:
                    print("같생 조회 비동기 성공")
                case .failure(let error):
                    print("같생 조회 비동기 error : \(error)")
                }
            }, receiveValue: { godsaengData in
                self.godsaeng = godsaengData
            })
            .store(in: &self.cancellables)
    }
    func requestGodsaengDetailFetch(accessToken: String, godsaengId: Int) -> Future<Godsaeng, Error> {
        return Future { promise in
            guard let url = URL(string: "\(requestURL)/godsaeng/\(godsaengId)") else {
                fatalError("같생 상세 조회 Invalid URL")
            }
            var request = URLRequest(url: url)
            request.httpMethod = "GET"
            request.addValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
                        
            URLSession.shared.dataTaskPublisher(for: request)
                .subscribe(on: DispatchQueue.global(qos: .background))
                .receive(on: DispatchQueue.main)
                .tryMap { data, response -> Data in
                    guard let httpResponse = response as? HTTPURLResponse else {
                        throw URLError(.badServerResponse)
                    }
                    switch httpResponse.statusCode {
                    case 200:
                        print("같생 상세 조회 상태코드 200")
                    case 401:
                        print("같생 상세 조회 상태코드 401")
                        AccessManager.shared.tokenExpired = true
                        AccessManager.shared.isLoggedIn = false
                    case 500 :
                        print("같생 상세 조회 서버 에러 500")
                        AccessManager.shared.serverDown = true
                        AccessManager.shared.isLoggedIn = false
                    default:
                        print("같생 상세 조회 상태코드: \(httpResponse.statusCode)")
                    }
                    return data
                }
                .decode(type: Godsaeng.self, decoder: JSONDecoder())
                .sink(receiveCompletion: { completion in
                    switch completion {
                    case .failure(let error):
                        promise(.failure( error))
                    case .finished:
                        print("같생 조회 요청 종료")
                        break
                    }
                }, receiveValue: { data in
                    promise(.success(data))
                })
                .store(in: &self.cancellables)
        }
    }
}
