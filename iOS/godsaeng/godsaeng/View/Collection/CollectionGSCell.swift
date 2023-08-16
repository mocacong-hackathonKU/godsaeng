//
//  GodsaengCard.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/13.
//

import SwiftUI

enum GodsaengCardType {
    case compact
    case extended
}

struct CollectionGSCell: View {
    
    @State var godsaeng: Godsaeng
    var mode: GodsaengCardType
    
    var body: some View {
        VStack(alignment: .leading) {
            //전체 카드
            HStack(alignment: .top) {
                //구분선
                Rectangle()
                    .fill(Color.mainOrange)
                    .frame(width: 4, height: 70)
                    .padding(.trailing)
                //내용
                VStack(alignment: .leading,spacing: 10) {
                    //윗줄
                    HStack {
                        //같생 제목
                        VStack(alignment: .leading) {
                            Text(godsaeng.title ?? "")
                                .foregroundColor(.black)
                                .font(.system(size: 20, weight: .semibold))
                            //같생 요일
                            VStack {
                                if godsaeng.weeks?.count == 7 {
                                    Text("매일")
                                } else {
                                    ForEach(godsaeng.weeks ?? [], id: \.self) { weekday in
                                        Text(weekday)
                                    }
                                }
                            }
                            .foregroundColor(.mainOrange)
                            .font(.system(size: 14))
                        }
                    }
                    //아럇줄
                    //같생 설명
                    Text(godsaeng.description ?? "")
                        .font(.system(size: 18))
                }
            }
        }
        .padding()
    }
}

