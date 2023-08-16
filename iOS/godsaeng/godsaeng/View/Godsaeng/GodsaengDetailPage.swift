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
            WeekDayCell()
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
    func WeekDayCell() -> some View {
        let weekdays = ["월", "화", "수", "목", "금", "토", "일"]
        var koreanWeeks = translateDays(days: godsaengVM.godsaeng.weeks ?? [])
        
        HStack(spacing: 10) {
            ForEach(weekdays, id: \.self) { day in
                Circle()
                    .frame(width: 41, height: 41)
                    .foregroundColor(koreanWeeks.contains(day) ? .mainOrange : .darkGray.opacity(0.2))
                    .overlay(
                        Text(day)
                            .font(.system(size: 15, weight: .semibold))
                            .foregroundColor(koreanWeeks.contains(day) ? .white : .black)
                    )
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
