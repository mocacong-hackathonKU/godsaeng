//
//  MyPage.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/15.
//

import SwiftUI
import PhotosUI
import SDWebImageSwiftUI

struct MyPage: View {
    
    @ObservedObject var memberVM: MemberViewModel
    @State var selectedPhotos: [PhotosPickerItem] = []
    @State var showProfileImageEditModal: Bool = false
    
    var body: some View {
        NavigationView {
            VStack {
                VStack(spacing: 12) {
                    if let imageData = memberVM.member.imgData, let uiImage = UIImage(data: imageData) {
                        Image(uiImage: uiImage)
                        .resizable()
                        .scaledToFill()
                        .frame(width: screenWidth * 0.45, height: screenWidth * 0.45)
                        .clipShape(Circle())
                        .clipped()
                        .overlay(
                            Circle()
                                .stroke(Color.darkGray.opacity(0.23), lineWidth: 0.8)
                                .foregroundColor(.clear)
                        )
                    }
                    Button(action: {
                        showProfileImageEditModal = true
                    }, label: {
                        Image(systemName: "pencil.circle.fill")
                            .font(.system(size: 20))
                    })
                    .offset(x: 50, y: -10)
                    Text(memberVM.member.nickname ?? "알 수 없음")
                        .foregroundColor(.black)
                        .font(.system(size: 20, weight: .medium))
                }
                .padding(.bottom, -35)
                //설정페이지
                SettingPage(memberVM: memberVM)
            }
            .padding(.top, 30)
        }
        .onAppear {
            if let token = try? TokenManager.shared.getToken() {
                memberVM.fetchMyProfileData(accessToken: token)
            }
        }
        .sheet(isPresented: $showProfileImageEditModal, content: {
            ProfileImageEditModal(memberVM: memberVM)
                .presentationDetents([.large, .fraction(0.6)])
        })
    }
}
