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
    
    var body: some View {
        VStack {
            TabView {
                LoginPage(memberVM: memberVM)
                    .tabItem({
                        Image(systemName: "person")
                    })
                RegisterPage()
                    .tabItem({
                        Image(systemName: "person.fill")
                    })
                AgreementView()
                    .tabItem({
                        Image(systemName: "person.fill")
                    })
                CalendarPage()
                    .tabItem({
                        Image(systemName: "person.fill")
                    })
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
