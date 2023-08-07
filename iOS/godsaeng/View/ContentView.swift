//
//  ContentView.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/07.
//

import SwiftUI

var screenWidth = UIScreen.main.bounds.width
var screenHeight = UIScreen.main.bounds.height

struct ContentView: View {
    var body: some View {
        TabView {
            CalendarView()
                .tabItem({
                    Image(systemName: "calendar")
                })
            CollectionView()
                .tabItem({
                    Image(systemName: "magnifyingglass")
                })
            MyPageView()
                .tabItem({
                    Image(systemName: "person.fill")
                })
        }
        .accentColor(Color.black)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
