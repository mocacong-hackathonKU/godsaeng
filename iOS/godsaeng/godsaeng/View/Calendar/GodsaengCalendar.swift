//
//  DatePicker.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/10.
//

import SwiftUI

struct GodsaengCalendar: View {
    
    @StateObject var godsaengVM: GodSaengViewModel = GodSaengViewModel()
    
    @State var currentDate: Date = Date()
    @State var currentMonth: Int = 0
    let days: [String] = ["Mon","Tue","Wed","Thu","Fri","Sat", "Sun"]
//    var godsaengs: [Godsaeng] = []
    
    var body: some View {
        ScrollView {
            VStack(spacing: 35) {
                HStack {
                    //달력 헤더
                    Button {
                            currentMonth -= 1
                    } label: {
                        Image("ArrowLeft")
                    }
                    Spacer()
                    VStack(spacing: 0) {
                        //년도
                        Text(getYearAndMonth(currentDate: currentDate)[0])
                            .font(.system(size: 16, weight: .medium))
                        //월
                        Text(getMonthFromDate(date: getCurrentMonth(currentMonth: currentMonth)))
                            .font(.system(size: 45, weight: .bold))
                            .foregroundColor(.mainOrange)
                    }
                    .offset(y: -12)
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
                LazyVGrid(columns: columns, spacing: 5) {
                    ForEach(extractDate(currentMonth: currentMonth)) { value in
                        VStack(spacing: 3) {
                            DayCell(value: value)
                                .background(
                                    Circle()
                                        .fill(.black)
                                        .opacity(isSameDay(date1: value.date, date2: currentDate) ? 1 : 0)
                                        .offset(y: -11)
                                )
                                .onTapGesture {
                                    currentDate = value.date
                                }
                            Rectangle()
                                .frame(width: 29, height: 2.5)
                                .foregroundColor(checkIsDone(value: value) == "true" ? .mainGreen : (checkIsDone(value: value)) == "false" ? .mainOrange : .clear)
                                .offset(y: -10)
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
            .padding(.vertical, 30)
        }
        .padding()
        //월 업데이트
        .onAppear {
            if let token = try? TokenManager.shared.getToken() {
                godsaengVM.fetchMonthlyGodsaengList(accessToken: token, currentMonth: "2023-08-01")
            }
        }
        .onChange(of: currentMonth) { newValue in
            currentDate = getCurrentMonth(currentMonth: currentMonth)
            let currentDateString = convertDateToString(date: currentDate)
            print("currentDate : ", currentDateString)
        }
    }
    
    @ViewBuilder
    func DayCell(value: DateValue) -> some View {
        VStack {
            if value.day != -1 {
                Text("\(value.day)")
                    .font(.title3.bold())
                    .foregroundColor(isSameDay(date1: value.date, date2: currentDate) ? .white : .primary)
                    .frame(maxWidth: .infinity)
                Spacer()
            }
        }
        .padding(.vertical, 9)
        .frame(height: 60, alignment: .top)
    }

    func checkIsDone(value: DateValue) -> String {
        
        var isDone: String = ""
        
        let matchingGodsaeng = godsaengVM.monthlyGodsaengList.first {
            $0.day == convertDateToString(date: value.date)
        }
        if let godsaeng = matchingGodsaeng {
            if godsaeng.isDone == true {
                isDone = "true"
            } else if godsaeng.isDone == false {
                isDone = "false"
            } else {
                isDone = "none"
            }
        }
        return isDone
    }
}

