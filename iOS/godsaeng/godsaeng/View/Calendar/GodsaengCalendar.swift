//
//  DatePicker.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/10.
//

import SwiftUI

struct GodsaengCalendar: View {
    @State var currentDate: Date = Date()
    @State var currentMonth: Int = 0
    let days: [String] = ["Mon","Tue","Wed","Thu","Fri","Sat", "Sun"]
    var godsaengs: [Godsaeng] = []
    
    var body: some View {
        ScrollView {
            VStack(spacing: 35) {
                
                //달력 헤더
                VStack(spacing: 5) {
                    
                    //년도
                    Text(getYearAndMonth(currentDate: currentDate)[0])
                        .font(.system(size: 25, weight: .semibold))
                    
                    //월
                    Text(getYearAndMonth(currentDate: currentDate)[1])
                        .font(.system(size: 30, weight: .bold))
                        .foregroundColor(.mainOrange)
                }
                
                //월 이동 버튼
                HStack {
                    Button {
                            currentMonth -= 1
                    } label: {
                        Image("ArrowLeft")
                    }
                    Spacer()
                    Button {
                            currentMonth += 1
                    } label: {
                        Image("ArrowRigth")
                    }
                }
                .padding(.horizontal, 20)
                
                //요일
                HStack(spacing: 0) {
                    ForEach(days, id: \.self){day in
                        Text(day)
                            .font(.callout)
                            .fontWeight(.semibold)
                            .frame(maxWidth: .infinity)
                    }
                }
                
                //날짜들
                let columns = Array(repeating: GridItem(.flexible()), count: 7)
                LazyVGrid(columns: columns,spacing: 15) {
                    ForEach(extractDate(currentMonth: currentMonth)) { value in

                        CardView(value: value)
                            .background(

                                Circle()
                                    .fill(.black)
                                    .opacity(isSameDay(date1: value.date, date2: currentDate) ? 1 : 0)
                            )
                            .onTapGesture {
                                currentDate = value.date
                            }
                    }
                }
                
                //오늘의 같생 목록
//                VStack(spacing: 24) {
//
//                    if let task = tasks.first(where: { task in
//                        return isSameDay(date1: task.taskDate, date2: currentDate)
//                    }) {
//                        ForEach(task.task) { task in
//
//                        }
//                    }
//                    else{
//                        Text("모아보기에서 같생에 참여할 수 있어요!")
//                    }
//                }
//                .padding()
            }
        }
        .padding()
        .onChange(of: currentMonth) { newValue in
            // updating Month...
            currentDate = getCurrentMonth(currentMonth: currentMonth)
        }
    }
    
    @ViewBuilder
    func CardView(value: DateValue)->some View{

        VStack{

            if value.day != -1{

//                if let godsaeng = godsaengs.first(where: { task in
//
//                    return isSameDay(date1: godsaeng, date2: value.date)
//                }){
//                    Text("\(value.day)")
//                        .font(.title3.bold())
//                        .foregroundColor(isSameDay(date1: task.taskDate, date2: currentDate) ? .white : .primary)
//                        .frame(maxWidth: .infinity)
//
//                    Spacer()
//
//                    Circle()
//                        .fill(isSameDay(date1: task.taskDate, date2: currentDate) ? .white : Color("Pink"))
//                        .frame(width: 8,height: 8)
//                }
//                else{

                    Text("\(value.day)")
                        .font(.title3.bold())
                        .foregroundColor(isSameDay(date1: value.date, date2: currentDate) ? .white : .primary)
                        .frame(maxWidth: .infinity)

                    Spacer()
//                }
            }
        }
        .padding(.vertical,9)
        .frame(height: 60,alignment: .top)
    }
//
//    // checking dates...
//    func isSameDay(date1: Date,date2: Date)->Bool{
//        let calendar = Calendar.current
//
//        return calendar.isDate(date1, inSameDayAs: date2)
//    }
//
//    //년도와 월 구하기
//    func getYearAndMonth() -> [String] {
//
//        let calendar = Calendar.current
//        let month = calendar.component(.month, from: currentDate) - 1
//        let year = calendar.component(.year, from: currentDate)
//
//        return ["\(year)", calendar.monthSymbols[month]]
//    }
//
//    //현재 월 구하기
//    func getCurrentMonth()->Date{
//
//        let calendar = Calendar.current
//        guard let currentMonth = calendar.date(byAdding: .month, value: self.currentMonth, to: Date()) else{
//            return Date()
//        }
//
//        return currentMonth
//    }
//
//    //날짜 추출
//    func extractDate() -> [DateValue] {
//
//        let calendar = Calendar.current
//        let currentMonth = getCurrentMonth()
//        var days = currentMonth.getAllDates().compactMap { date -> DateValue in
//            let day = calendar.component(.day, from: date)
//            return DateValue(day: day, date: date)
//        }
//        let firstWeekday = calendar.component(.weekday, from: days.first!.date)
//
//        for _ in 0..<firstWeekday - 1{
//            days.insert(DateValue(day: -1, date: Date()), at: 0)
//        }
//
//        return days
//    }
}

