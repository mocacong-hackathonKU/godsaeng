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
    @State var profileImageDataToUpdate: Data?
    @State var selectedPhotos: [PhotosPickerItem] = []
    @State var showProfileImageEditModal: Bool = false
    
    var body: some View {
        VStack {
            WebImage(url: URL(string: memberVM.member.imgUrl ?? ""))
                .resizable()
                .scaledToFit()
                .frame(width: screenWidth * 0.48)
                .clipShape(Circle())
                .padding(.top, -50)
        }
        .overlay (
            HStack {
                Spacer()
                Button(action: {
                    showProfileImageEditModal = true
                }, label: {
                    Image(systemName: "pencil.circle.fill")
                        .font(.system(size: 20, weight: .medium))
                })
                .offset(y: 50)
            }
        )
        .sheet(isPresented: $showProfileImageEditModal, content: {
            ProfileImageEditModal(memberVM: memberVM)
                .presentationDetents([.large, .fraction(0.6)])
        })
    }
}
