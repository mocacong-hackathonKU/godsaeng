//
//  LoginPage.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/07.
//

import SwiftUI

struct LoginPage: View {
    
    @ObservedObject var memberVM: MemberViewModel
    
    var body: some View {
        ZStack {
            Image("LoginBackground")
                .resizable()
                .ignoresSafeArea()
            Button(action: {
                memberVM.loginApple()
            }, label: {
                AppleDefaultIcon()
                    .padding(.top, 50)
            })
        }
    }
    
    @ViewBuilder
    func AppleDefaultIcon() -> some View {
        RoundedRectangle(cornerRadius: 6.3)
            .frame(width: screenWidth * 0.8, height: 48)
            .foregroundColor(.black)
            .overlay(
                HStack {
                    Image(systemName: "apple.logo")
                        .foregroundColor(.white)
                        .font(.system(size: 19))
                        .offset(y: -1.5)
                    Text("Login with Apple")
                        .foregroundColor(.white)
                        .font(.system(size: 17))
                }
            )
    }
}

struct LoginPage_Previews: PreviewProvider {
    static var previews: some View {
        LoginPage(memberVM: MemberViewModel())
    }
}
