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
            HStack(alignment: .top, spacing: 20) {
                 Rectangle()
                     .fill(Color.mainOrange)
                     .frame(width: 3)
                 VStack {
                     HStack(spacing: 10) {
                         //같생 제목
                         VStack(alignment: .leading) {
                             Text(godsaeng.title ?? "")
                                 .font(.title2.bold())
                             //같생 요일
                             if godsaeng.weeks?.count == 7 {
                                 Text("매일")
                             } else {
                                 ForEach(godsaeng.weeks ?? [], id: \.self) { weekday in
                                     Text(weekday)
                                 }
                             }
                         }
                         .foregroundColor(.mainOrange)
                         .font(.system(size: 12))
                         //같생 설명
                         if mode == .extended {
                             Text(godsaeng.description ?? "")
                                 .foregroundStyle(.secondary)
                         }
                     }
                 }
                 .padding()
             }
    }
}

