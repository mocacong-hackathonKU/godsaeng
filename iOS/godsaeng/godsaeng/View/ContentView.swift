//
//  ContentView.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/07.
//

import SwiftUI

let requestURL = Bundle.main.object(forInfoDictionaryKey: "REQUEST_URL") as? String ?? ""

var screenWidth = UIScreen.main.bounds.width
var screenHeight = UIScreen.main.bounds.height

struct ContentView: View {
    
    @StateObject var memberVM: MemberViewModel = MemberViewModel()
    @State var showLoginRequestAlert: Bool = false
    @State var showServerDownAlert: Bool = false
    
    var body: some View {
        VStack {
            if AccessManager.shared.isLoggedIn == true {
                if AccessManager.shared.isRegistered == false && AccessManager.shared.isAgreed == false {
                    AgreementPage()
                } else if AccessManager.shared.isRegistered == false && AccessManager.shared.isAgreed == true {
                    RegisterPage()
                } else if AccessManager.shared.isRegistered == true {
                    TabView {
                        CalendarPage()
                            .tabItem({
                                Image(systemName: "calendar")
                            })
                        CollectionPage()
                            .tabItem({
                                Image(systemName: "magnifyingglass")
                            })
                        MyPage()
                            .tabItem({
                                Image(systemName: "person.fill")
                            })
                    }
                    .accentColor(Color.darkGray)
                }
            } else {
                LoginPage(memberVM: memberVM)
            }
        }
        .onChange(of: AccessManager.shared.tokenExpired, perform: { newValue in
            if newValue == true {
                showLoginRequestAlert = true
            }
        })
        .onChange(of: AccessManager.shared.serverDown, perform: { newValue in
            if newValue == true {
                showServerDownAlert = true
            }
        })
        .alert(isPresented: $showLoginRequestAlert) {
            Alert(title: Text("알림"), message: Text("로그인이 필요한 서비스입니다"), dismissButton: .default(Text("확인")))
        }
        .alert(isPresented: $showServerDownAlert) {
            Alert(title: Text("알림"), message: Text("일시적으로 접속이 원활하지 않습니다. 같생 서비스팀에 문의 부탁드립니다"), dismissButton: .default(Text("확인")))
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
