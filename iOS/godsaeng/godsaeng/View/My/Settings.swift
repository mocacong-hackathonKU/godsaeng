//
//  SettingPage.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/15.
//

import SwiftUI

struct SettingPage: View {
    
    @ObservedObject var memberVM: MemberViewModel
    var version: String = "1.0.0"
    
    var body: some View {
            VStack {
                VStack(spacing: 0) {
                    NavigationLink(destination: {
                        AcountPage(memberVM: memberVM)
                    }, label: {
                        BlockCell(icon: "person", label: "계정 정보")
                    })
                    Button(action: {
                        if let url = URL(string: "https://www.notion.so/mocacong/Q-A-2c8e8ad6c2604245ba1610fdbcfcc7e2") {
                            UIApplication.shared.open(url)
                        }
                    }, label: {
                        BlockCell(icon: "questionmark.circle", label: "Q & A")
                        
                    })
                    Button(action: {
                        if let url = URL(string: "https://www.notion.so/mocacong/78a169a2532a4e9e94fe2ae2da41c6a4") {
                            UIApplication.shared.open(url)
                        }
                    }, label: {
                        BlockCell(icon: "doc.text.magnifyingglass", label: "이용약관")
                    })
                    Button(action: {
                        if let url = URL(string: "https://www.notion.so/mocacong/ef1c29e4c9954d3e907936e955a1b8a0") {
                            UIApplication.shared.open(url)
                        }
                    }, label: {
                        BlockCell(icon: "lock", label: "개인정보처리방침")
                    })
                    Button(action: {
                        if let url = URL(string: "https://www.notion.so/mocacong/053df0bda1674234a5252d8bc82a4b7b") {
                            UIApplication.shared.open(url)
                        }
                    }, label: {
                        BlockCell(icon: "lock", label: "개인정보수집및이용동의서")
                    })
                    Button(action: {
                        if let url = URL(string: "https://www.notion.so/mocacong/e83a984e6d644772b4f819d40fd8dc72") {
                            UIApplication.shared.open(url)
                        }
                    }, label: {
                        BlockCell(icon: "megaphone", label: "공지사항")
                    })
                    BlockCell(icon: "info.circle", label: "문의처")
                        .padding(.bottom)
                    Text("버전 정보 : \(version)")
                        .foregroundColor(.gray.opacity(0.9))
                        .font(.system(size: 13.5))
                }
                .padding(.top, 40)
            }

    }
    
    @ViewBuilder
    func BlockCell(icon: String, label: String) -> some View {
        VStack {
            Rectangle()
                .stroke(Color.gray.opacity(0.5), lineWidth: 0.2)
                .foregroundColor(.clear)
                .frame(width: screenWidth, height: 55)
                .overlay(
                    HStack(spacing: 15) {
                        Image(systemName: icon)
                            .font(.system(size: 18))
                            .padding(.leading)
                        Text(label)
                            .font(.system(size: 17))
                        Spacer()
                        if label == "계정 정보" {
                            Image(systemName: "chevron.right")
                                .font(.system(size: 17))
                                .padding(.trailing)
                        }
                        if label == "문의처" {
                            Text("official.godsaeng@gmail.com")
                                .foregroundColor(.gray.opacity(0.8))
                                .font(.system(size: 14))
                                .padding(.trailing)
                        }
                    }
                )
        }
    }
}
