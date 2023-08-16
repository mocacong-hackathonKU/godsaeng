//
//  Godsaeng.swift
//  godsaeng
//
//  Created by Suji Lee on 2023/08/10.
//

import Foundation

struct Godsaeng: Identifiable, Codable, Hashable {
    var id: Int?
    var title: String?
    var description: String?
    var openDate: String?
    var closeDate: String?
    var weeks: [String]?
    var members: [Member]?
    var progress: Int?
    var status: String?
    var proofs: [Proof]?
    var isDone: Bool?
}
