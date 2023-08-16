//
//  GodsaengDetailPage.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/16.
//

import SwiftUI
import SDWebImageSwiftUI

struct GodsaengDetailPage: View {
    
    @ObservedObject var godsaengVM: GodSaengViewModel
    @State var godsaeng: Godsaeng
    
    var body: some View {
        VStack {
            //같생 제목
            Text(godsaengVM.godsaeng.title ?? "")
            //같생 기간
            Text(godsaeng.openDate ?? "")
            Text(godsaeng.closeDate ?? "")
            //같생 요일
            WeekDateCell(weekDate: godsaeng.weeks ?? [])
            //같생 설명
            Text(godsaeng.description ?? "")
            //같생 참여자
            ScrollView {
                HStack(spacing: 12) {
                    ForEach(godsaengVM.godsaeng.members ?? []) { member in
                        MemberCell(member: member)
                    }
                }
            }
            //같생 진척도
            VStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: 5)
                    .frame(width: screenWidth * 9, height: 10)
                    .foregroundColor(.lightGray)
                    .overlay(
                        RoundedRectangle(cornerRadius: 5)
                            .frame(width: screenWidth * 9 * CGFloat((godsaeng.progress ?? 0)/100) , height: 10)
                            .foregroundColor(.mainGreen)
                    )
            }
            //인증셀 리스트
        }
        .onAppear {
            if let token = try? TokenManager.shared.getToken() {
                godsaengVM.fetchGodsaengDetail(accessToken: token, godsaengId: godsaeng.id ?? 0)
            }
        }
    }
    
    @ViewBuilder
    func WeekDateCell(weekDate: [String]) -> some View {
        
        let weekDates = ["월", "화", "수", "목", "금", "토", "일"]
        
        HStack {
            ForEach(weekDates, id: \.self) { day in
                if weekDate.contains(day) {
                    Circle()
                        .frame(width: 30, height: 30)
                        .foregroundColor(.mainOrange)
                        .overlay(
                            Text(day)
                                .foregroundColor(.white)
                        )
                } else {
                    Circle()
                        .frame(width: 30, height: 30)
                        .foregroundColor(.lightGray)
                        .overlay(
                            Text(day)
                                .foregroundColor(.black)
                        )
                }
            }
        }
    }

    
    @ViewBuilder
    func MemberCell(member: Member) -> some View {
        VStack {
            WebImage(url: URL(string: member.imgUrl ?? ""))
                .resizable()
                .clipShape(RoundedRectangle(cornerRadius: 6))
                .frame(width: 35, height: 35)
            Text(member.nickname ?? "알 수 없음")
        }
    }
    
}
