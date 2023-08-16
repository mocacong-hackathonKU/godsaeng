//
//  RegisterPage.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/07.
//

import SwiftUI
import PhotosUI
import UIKit

struct RegisterPage: View {
    
    @ObservedObject var memberVM: MemberViewModel
    
    @Binding var memberToRegister: Member
    
    @State var nickname: String = ""
    @State var profileImageData: Data?
    @State var selectedPhotos: [PhotosPickerItem] = []
    
    @State var isDuplicated: Bool?
    @State var textInputAccepted: Bool = false
    @State var nicknameHasNumber: Bool = false
    @State var nicknameHasSpecial: Bool = false
    
    var body: some View {
        NavigationView {
            VStack {
                VStack {
                Text("회원가입")
                    .font(.system(size: 27, weight: .bold))
                    .padding(.top, -20)
                    .padding(.bottom, 45)
                    VStack(alignment: .leading) {
                        Text("프로필 설정")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(.accent4)
                            .padding(.bottom, 7)
                        Text("나를 잘 나타내는 사진과 닉네임을 정해주세요")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.darkGray)
                        Text("프로필은 같생을 함께할 사람들에게 보여집니다")
                            .font(.system(size: 14))
                            .foregroundColor(.darkGray)
                    }
                    .padding(.leading, -70)
                }
                .padding(.bottom, 35)
                VStack {
                    //이미지
                    if let imageData = profileImageData, let uiImage = UIImage(data: imageData) {
                        Image(uiImage: uiImage)
                            .resizable()
                            .scaledToFit()
                            .frame(width: screenWidth * 0.48)
                            .clipShape(Circle())
                            .padding(.top, -50)
                            .overlay(
                                PhotosPicker(selection: $selectedPhotos, maxSelectionCount: 1, matching: .images) {
                                    Circle()
                                        .foregroundColor(.clear)
                                        .frame(width: 300)
                                        .offset(y: -30)
                                }
                                    .onChange(of: selectedPhotos) { newItem in
                                        guard let item = selectedPhotos.first else {
                                            return
                                        }
                                        item.loadTransferable(type: Data.self) { result in
                                            switch result {
                                            case .success(let data):
                                                if let data = data {
                                                    self.profileImageData = data
                                                } else {
                                                    print("data is nil")
                                                }
                                            case .failure(let failure):
                                                fatalError("\(failure)")
                                            }
                                        }
                                    }
                            )
                    } else {
                        Image("DefaultProfile")
                            .resizable()
                            .scaledToFit()
                            .frame(width: screenWidth * 0.48)
                            .clipShape(Circle())
                            .padding(.top, -50)
                            .overlay(
                                PhotosPicker(selection: $selectedPhotos, maxSelectionCount: 1, matching: .images) {
                                    Circle()
                                        .foregroundColor(.clear)
                                        .frame(width: 300)
                                        .offset(y: -30)
                                }
                                    .onChange(of: selectedPhotos) { newItem in
                                        guard let item = selectedPhotos.first else {
                                            return
                                        }
                                        item.loadTransferable(type: Data.self) { result in
                                            switch result {
                                            case .success(let data):
                                                if let data = data {
                                                    self.profileImageData = data
                                                } else {
                                                    print("data is nil")
                                                }
                                            case .failure(let failure):
                                                fatalError("\(failure)")
                                            }
                                        }
                                    }
                            )
                    }
                }
                .padding(.bottom, 40)
                VStack(alignment: .leading) {
                    TextField("닉네잉 (변경불가)", text: $nickname)
                    Rectangle()
                        .foregroundColor(.lightGray)
                        .frame(width: 340, height: 3)
                    VStack(alignment: .leading) {
                        Text("닉네임은 한글과 영어 2~6자로 입력해주세요")
                        Text("중복된 닉네임입니다")
                    }
                    .font(.system(size: 13))
                    .foregroundColor(.alertRed)
                }
                .padding(.leading, 30)
                
                VStack(alignment: .leading, spacing: 10) {
                    //닉네임 레이블
                    HStack(spacing: 17) {
                        Text("예시) 프로카공러")
                            .font(.system(size: 15))
                            .foregroundColor(.black.opacity(0.7))
                        Spacer()
                    }
                    //닉네임 입력창
                    TextField("닉네잉 (변경불가)", text: $nickname)
                    Rectangle()
                        .foregroundColor(.lightGray)
                        .frame(width: 340, height: 3)                            .onAppear {
                                UIApplication.shared.hideKeyboard()
                            }
                            .onChange(of: nickname, perform: { newValue in
                                checkNicknameDuplication(nickname: newValue)
                                print(newValue)
                            })
                            .onChange(of: nickname) { val in
                                //닉네임 개수 검사
                                if val.count >= 2 && val.count <= 5 {
                                    textInputAccepted = true
                                } else {
                                    textInputAccepted = false
                                }
                                //닉네임 숫자 검사
                                let numbers = CharacterSet.decimalDigits
                                nicknameHasNumber = val.unicodeScalars.contains(where: numbers.contains)
                                if nicknameHasNumber == true {
                                    textInputAccepted = false
                                } else {
                                    textInputAccepted = true
                                }
                                //닉네임 특수문자 검사
                                let specialCharacter = CharacterSet(charactersIn: " !\"#$%&'()*+,-./:;<=>?@[]^_`{|}~")
                                nicknameHasSpecial =  val.unicodeScalars.contains(where: specialCharacter.contains)
                                if nicknameHasSpecial == true {
                                    textInputAccepted = false
                                } else {
                                    textInputAccepted = true
                                }
                            }
                    
                    VStack(alignment: .leading) {
                        if nickname.count < 2 || nickname.count > 5 || nicknameHasNumber == true || nicknameHasSpecial == true {
                            Text("닉네임은 한글, 영어로만 구성된 2~5자여야 합니다")
                                .font(.system(size: 13))
                                .foregroundColor(.alertRed)
                        } else {
                            if isDuplicated != nil {
                                if isDuplicated == true{
                                    Text("중복된 닉네임입니다")
                                        .font(.system(size: 13))
                                        .foregroundColor(.alertRed)
                                } else if isDuplicated == false {
                                    Text("사용할 수 있는 닉네임입니다")
                                        .font(.system(size: 13))
                                        .foregroundColor(.mainGreen)
                                }
                            }
                        }
                    }
                    .padding(.bottom, 40)
                }
            }
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        postNewMember()
                    }, label: {
                        Text("완료")
                            .foregroundColor(.mainGreen)
                    })
                    .disabled(nickname.trimmingCharacters(in: .whitespaces) == "" || isDuplicated == nil || isDuplicated == true || textInputAccepted == false)
                }
            }
        }
    }
    
    func checkNicknameDuplication(nickname: String) {
        memberVM.checkNicknameDuplicationToServer(nicknameToCheck: nickname)
            .sink(receiveCompletion: { completion in
                switch completion {
                case .failure(let error):
                    print("중복검사 비동기 실패")
                    print("중복검사 Error: \(error.localizedDescription)")
                case .finished:
                    if let isDuplicated = memberVM.isDuplicated {
                        self.isDuplicated = isDuplicated
                    }
                    print("Nickname check completed.")
                }
            }, receiveValue: { isDuplicate in
                
            })
            .store(in: &memberVM.cancellables)
    }
    
    func postNewMember() {
        memberToRegister.nickname = self.nickname
        memberToRegister.imgData = self.profileImageData
        if memberToRegister.nickname != nil && memberToRegister.imgData != nil {
            memberVM.requestRegisterToServer(memberToRegister: memberToRegister)
                .sink(receiveCompletion: { result in
                    switch result {
                    case .failure(let error):
                        print("회원가입 error: \(error)")
                    case .finished:
                        break
                    }
                }, receiveValue: { data in
                })
                .store(in: &memberVM.cancellables)
        }
    }
}
