//
//  ContentView.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/07.
//

import SwiftUI

var screenWidth = UIScreen.main.bounds.width
var screenHeight = UIScreen.main.bounds.height

struct ContentView: View {
    
    @StateObject var memberVM: MemberViewModel = MemberViewModel()
    
    var body: some View {
        VStack {
            TabView {
                LoginPage()
                    .tabItem({
                        Image(systemName: "person")
                    })
                RegisterPage()
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
