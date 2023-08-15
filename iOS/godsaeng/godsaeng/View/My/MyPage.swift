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
    
    var body: some View {
        VStack {
            WebImage(url: URL(string: memberVM.member.imgUrl ?? ""))
                .resizable()
                .scaledToFit()
                .frame(width: screenWidth * 0.48)
                .clipShape(Circle())
                .padding(.top, -50)
                .overlay(
                    PhotosPicker(selection: $selectedPhotos, maxSelectionCount: 1, matching: .images) {
                        Image(systemName: "pencil.circle.fill")
                            .font(.system(size: 20, weight: .medium))
                    }
                        .onChange(of: selectedPhotos) { newItem in
                            guard let item = selectedPhotos.first else {
                                return
                            }
                            item.loadTransferable(type: Data.self) { result in
                                switch result {
                                case .success(let data):
                                    if let data = data {
                                        self.profileImageDataToUpdate = data
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
        .overlay(
            HStack {
                Spacer()
                Button(action: {
                    showImageEditModal = true
                }, label: {
                    Image(systemName: "pencil.circle.fill")
                        .font(.system(size: 32))
                        .foregroundColor(.ssook)
                        .background(Color.white, in: Circle())
                        .clipped()
                })
                .offset(y: 50)
            }
        )
    }
}
