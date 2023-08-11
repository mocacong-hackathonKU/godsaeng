//
//  AgreementView.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/10.
//

import SwiftUI

struct AgreementView: View {
    
    @EnvironmentObject var accessManager: AccessManager
    var allChecked: Bool {
        if self.utilizationContractChecked && self.personalInfoContractChecked {
            return true
        } else {
            return false
        }
    }
    @State var utilizationContractChecked: Bool = false
    @State var personalInfoContractChecked: Bool = false
    var fontSize: CGFloat = 18
    
    var body: some View {
        NavigationView {
            VStack {
                VStack(alignment: .leading) {
                    Text("같생")
                        .font(.system(size: 33, weight: .bold))
                        .foregroundColor(.mainOrange)
                    VStack(alignment: .leading) {
                        Text("서비스 이용을 위해")
                        Text("아래 항목에 동의해주세요")
                    }
                    .font(.system(size: 23))
                }
                .padding(.top, -30)
                .padding(.leading, -30)
                .padding(.bottom, 50)
                VStack {
                    HStack {
                        Spacer()
                        Text("모두 동의")
                            .foregroundColor(.mainGreen)
                            .font(.system(size: fontSize, weight: .semibold))
                        Button(action: {
                            withAnimation(.spring()) {
                                if allChecked == true {
                                    utilizationContractChecked = false
                                    personalInfoContractChecked = false
                                } else {
                                    utilizationContractChecked = true
                                    personalInfoContractChecked = true
                                }
                            }
                        }, label: {
                            Image("SquareBox")
                                .resizable()
                                .frame(width: 25, height: 25)
                                .overlay {
                                    Image(systemName: allChecked == true ? "checkmark" : "")
                                        .foregroundColor(.alertRed)
                                        .font(.system(size: 15, weight: .semibold))
                                }
                        })
                    }
                    Divider()
                        .padding(.vertical)
                    VStack(alignment: .leading, spacing: 25) {
                        HStack {
                            Text("이용약관 동의")
                                .foregroundColor(.accent5)
                                .font(.system(size: fontSize, weight: .semibold))
                            Spacer()
                            Button(action: {
                                if let url = URL(string: "https://www.notion.so/mocacong/78a169a2532a4e9e94fe2ae2da41c6a4") {
                                    UIApplication.shared.open(url)
                                }
                            }, label: {
                                Text("내용보기")
                                    .foregroundColor(.darkGray)
                                    .font(.system(size: 14))
                            })
                            Button(action: {
                                withAnimation(.spring()) {
                                    utilizationContractChecked.toggle()
                                }
                            }, label: {
                                Image("SquareBox")
                                    .resizable()
                                    .frame(width: 25, height: 25)
                                    .overlay {
                                        Image(systemName: utilizationContractChecked == true ? "checkmark" : "")
                                            .foregroundColor(.alertRed)
                                            .font(.system(size: 15, weight: .semibold))
                                    }
                            })
                        }
                        //개인정보 수집 및 이용 동의
                        HStack {
                            Text("개인정보 수집 및 이용 동의")
                                .foregroundColor(.accent5)
                                .font(.system(size: fontSize, weight: .semibold))
                            Spacer()
                            Button(action: {
                                if let url = URL(string: "https://www.notion.so/mocacong/053df0bda1674234a5252d8bc82a4b7b") {
                                    UIApplication.shared.open(url)
                                }
                            }, label: {
                                Text("내용보기")
                                    .foregroundColor(.darkGray)
                                    .font(.system(size: 14))
                            })
                            Button(action: {
                                withAnimation(.spring()) {
                                    personalInfoContractChecked.toggle()
                                }
                            }, label: {
                                Image("SquareBox")
                                    .resizable()
                                    .frame(width: 25, height: 25)
                                    .overlay {
                                        Image(systemName: personalInfoContractChecked == true ? "checkmark" : "")
                                            .foregroundColor(.alertRed)
                                            .font(.system(size: fontSize, weight: .semibold))
                                    }
                            })
                        }
                    }
                }
                .padding(.bottom, 30)
                Button(action: {
                    accessManager.isAgreed = true
                }, label: {
                    RoundedRectangle(cornerRadius: 20)
                        .foregroundColor(allChecked == true ? .mainGreen : .gray.opacity(0.5))
                        .frame(width: screenWidth * 0.9, height: 47)
                        .overlay (
                            Text("가입하기")
                                .foregroundColor(.white)
                                .font(.system(size: 19, weight: .semibold))
                        )
                })
                .disabled(allChecked == false)
            }
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading, content: {
                    Button(action: {
                        AccessManager.shared.isAgreed = false
                        AccessManager.shared.isLoggedIn = false
                    }, label: {
                        Text("취소")
                            .foregroundColor(.darkGray)
                    })
                })
            }
        }
    }
}

struct AgreementView_Previews: PreviewProvider {
    static var previews: some View {
        AgreementView()
    }
}
