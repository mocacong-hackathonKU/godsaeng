//
//  GodsaengCellInCalendar.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/16.
//

import SwiftUI

struct CalendarGSCell: View {
    
    @State var godsaeng: Godsaeng
    
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 10)
                .stroke(Color.lightGray, lineWidth: 1.2)
                .frame(width: screenWidth * 0.85, height: 65)
                .foregroundColor(.clear)
            HStack {
                Text(godsaeng.title ?? "")
                    .font(.system(size: 34, weight: .bold))
                Button(action: {
                    
                }, label: {
                    RoundedRectangle(cornerRadius: 20)
                        .frame(width: 68, height: 25)
                        .foregroundColor(godsaeng.isDone ?? true ? .mainGreen : .mainOrange)
                        .overlay (
                            Text("인증하기")
                                .foregroundColor(.white)
                                .font(.system(size: 14, weight: .medium))
                        )
                })
            }
            .frame(width: screenWidth * 0.85, height: 65)
        }
    }
}
