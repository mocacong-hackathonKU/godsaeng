////
////  GodsaengList.swift
////  godsaeng
////
////  Created by Suji Lee on 2023/08/13.
////ㅌ
//
//import SwiftUI
//
//struct DailyGodsaengList: View {
//
//    @ObservedObject var godsaengVM: GodSaengViewModel
//
//    var body: some View {
//        VStack(spacing: 24) {
//            if let godsaeng = godsaengVM.dailyGodsaengList.first(where: { godsaeng in
//                return isSameDay(date1: godsaeng.dat, date2: currentDate)
//            }) {
//                ForEach(task.task) { task in
//
//                }
//            }
//            else{
//                Text("모아보기에서 같생에 참여할 수 있어요!")
//            }
//        }
//        .padding()
//    }
//}
//
//struct DailyGodsaengList_Previews: PreviewProvider {
//    static var previews: some View {
//        DailyGodsaengList(godsaengVM: GodSaengViewModel())
//    }
//}
