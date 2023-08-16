//
//  ProofCreateModal.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/16.
//

import SwiftUI

struct ProofPostModal: View {
    
    @Environment(\.dismiss) private var dismiss
    @ObservedObject var godsaengVM: GodSaengViewModel
    @StateObject var proofVM: ProofViewModel = ProofViewModel()
    @State var proofImg: Data?
    @State var content: String = ""
    
    var body: some View {
        VStack {
                Image("ProofImgTemplate")
                    .resizable()
                    .scaledToFit()
                    .frame(width: screenWidth * 0.85)
            }
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading, content: {
                Button("취소") {
                    dismiss()
                }
            })
            ToolbarItem(placement: .navigationBarTrailing, content: {
                Button("저장") {
                    if let token = try? TokenManager.shared.getToken() {
                        proofVM.createProof(accessToken: token, godsaeng: godsaengVM.godsaeng)
                    }
                    dismiss()
                }
                .disabled(content.trimmingCharacters(in: .whitespaces) == "" || content.count > 25)
            })
        }

    }
}

