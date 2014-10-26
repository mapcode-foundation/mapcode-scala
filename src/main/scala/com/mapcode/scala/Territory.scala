/*
 * Copyright (C) 2014 Stichting Mapcode Foundation (http://www.mapcode.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mapcode.scala

/**
 * Enumeration that specifies the format for mapcodes.
 */
object NameFormat extends Enumeration {
  type NameFormat = Value
  val International, MinimalUnambiguous, Minimal = Value
}

// This is broken out of Territory, because when we run scoverage, it generates a class file too big for the
// JVM to handle. We've excluded Territory itself from the coverage tests, but it's nice to be able to see
// coverage for the code in this trait
trait TerritoryOperations extends Enumeration {

  import com.mapcode.scala.NameFormat.NameFormat

  lazy val territories: Seq[Territory] = values.toSeq.map(_.asInstanceOf[Territory])
  private[scala] lazy val codeList: Map[Int, Territory] = territories.map(t => t.id -> t).toMap
  private[scala] lazy val nameMap: Map[String, Seq[Territory]] = {
    def populateTerritory(map: Map[String, Seq[Territory]], territory: Territory): Map[String, Seq[Territory]] = {
      territory.allNames.foldLeft(map)(addName(territory))
    }
    territories.foldLeft(Map[String, Seq[Territory]]())(populateTerritory)
  }

  private[scala] lazy val parentTerritories: Set[Territory] = territories.flatMap(_.parentTerritory).toSet

  def fromTerritoryCode(territoryCode: Int): Option[Territory] = codeList.get(territoryCode)

  /**
   * Get a territory from a mapcode territory abbreviation. Note that the provided abbreviation is NOT an
   * ISO code: it's a mapcode prefix. As local mapcodes for states have been optimized to prefer to use 2-character
   * state codes in local codes, states are preferred over countries in this case.
   *
   * For example, fromString("AS") returns IN_AS rather than ASM and fromString("BR") returns IN_BR rather than BRA.
   *
   * This behavior is intentional as local mapcodes are designed to be as short as possible. A mapcode within
   * the Indian state Bihar should therefore be able to specified as "BR 49.46M3" rather "IN-BR 49.46M3".
   *
   * Brazilian mapcodes, on the other hand, would be specified as "BRA BDHP.JK39-1D", using the ISO 3 letter code.
   */
  def fromString(name: String, parentTerritory: Option[Territory] = None): Option[Territory] = {
    parentTerritory.foreach(pt => require(parentTerritories.contains(pt), s"$pt is not a valid parent territory"))
    (nameMap.get(name), parentTerritory) match {
      case (Some(terrs), None) => terrs.find(_.name == name) orElse terrs.find(_.parentTerritory.isDefined) orElse terrs.headOption
      case (Some(terrs), parent@Some(_)) =>
        terrs.find(_.parentTerritory == parent)
      case _ =>
        // Check for a case such as USA-NLD (=NLD)
        val dividerLocation = math.max(name.indexOf('-'), name.indexOf(' '))
        if (dividerLocation >= 0) fromString(name.substring(dividerLocation + 1), parentTerritory)
        else None
    }
  }

  private[scala] def addName(territory: Territory)
                            (nameMap: Map[String, Seq[Territory]], name: String): Map[String, Seq[Territory]] = {
    // Add child territories in the order the parents are declared.
    // This results in consistent decoding of ambiguous territory names.
    nameMap.get(name).fold(nameMap + (name -> Seq(territory))) { terrs =>
      territory.parentTerritory.map { parent =>
        (nameMap - name) + (name -> (terrs :+ territory).distinct)
      }.getOrElse(sys.error("You can't have multiple top-level territories with the same name"))
    }
  }

  case class Territory private[scala](territoryCode: Int,
                                      fullName: String,
                                      parentTerritory: Option[Territory] = None,
                                      aliases: Seq[String] = Seq.empty,
                                      fullNameAliases: Seq[String] = Seq.empty) extends Val {

    /**
     * Return the territory name, given a specific territory name format.
     *
     * @param format Format to be used.
     * @return Mapcode
     */
    def toNameFormat(format: NameFormat): String = {
      format match {
        case NameFormat.International => name
        case _ =>
          if (name.contains('-')) {
            val shortName = name.dropWhile(_ != '-').tail
            if (format == NameFormat.Minimal || nameMap(shortName).size == 1) shortName
            else name
          }
          else name
      }
    }

    def name: String = toString().replaceAll("_", "-")

    def isState: Boolean = parentTerritory.isDefined

    def hasStates: Boolean = parentTerritories.contains(this)

    def allNames: Seq[String] = {
      def spaceDup(name: String): Seq[String] = {
        if (name.contains('-')) Seq(name, name.replace('-', ' '))
        else Seq(name)
      }
      def generateAliasCombos(name: String): Seq[String] = {
        // 3-letter codes are not that interesting, since they are more useful
        // for locating countries instead of states. So we just focus on aliases
        // that start with a 2-letter iso code. Which begs the question when are
        // they useful, and whether; not sure this is right yet. todo
        val suffix: Option[String] = if (name.indexOf("-") == 2) Some(name.dropWhile(_ != '-').tail) else None
        val parentVariant: Option[String] = for (sfx <- suffix; par <- parentTerritory.map(_.name)) yield s"$par-$sfx"
        Seq(name) ++ suffix ++ parentVariant
      }

      (generateAliasCombos(name) ++ aliases.flatMap(generateAliasCombos)).flatMap(spaceDup).distinct
    }
  }

}

/**
 * This class defines the available territory codes as used by mapcode.
 */
object Territory extends TerritoryOperations {

  val USA = Territory(409, "USA", None, Seq("US"), Seq("United States of America", "America"))
  val IND = Territory(406, "India", None, Seq("IN"))
  val CAN = Territory(494, "Canada", None, Seq("CA"))
  val AUS = Territory(407, "Australia", None, Seq("AU"))
  val MEX = Territory(410, "Mexico", None, Seq("MX"))
  val BRA = Territory(408, "Brazil", None, Seq("BR"))
  val RUS = Territory(495, "Russia", None, Seq("RU"))
  val CHN = Territory(527, "China", None, Seq("CN"))
  val ATA = Territory(539, "Antarctica")
  val VAT = Territory(0, "Vatican City", None, Seq.empty, Seq("Holy See"))
  val MCO = Territory(1, "Monaco")
  val GIB = Territory(2, "Gibraltar")
  val TKL = Territory(3, "Tokelau")
  val CCK = Territory(4, "Cocos Islands", None, Seq("AU-CC", "AUS-CC"), Seq("Keeling Islands"))
  val BLM = Territory(5, "Saint-Barthelemy")
  val NRU = Territory(6, "Nauru")
  val TUV = Territory(7, "Tuvalu")
  val MAC = Territory(8, "Macau", None, Seq("CN-92", "CHN-92", "CN-MC", "CHN-MC"))
  val SXM = Territory(9, "Sint Maarten")
  val MAF = Territory(10, "Saint-Martin")
  val NFK = Territory(11, "Norfolk and Philip Island", None, Seq("AU-NI", "AUS-NI", "AU-NF", "AUS-NF"), Seq("Philip Island"))
  val PCN = Territory(12, "Pitcairn Islands")
  val BVT = Territory(13, "Bouvet Island")
  val BMU = Territory(14, "Bermuda")
  val IOT = Territory(15, "British Indian Ocean Territory", None, Seq("DGA"))
  val SMR = Territory(16, "San Marino")
  val GGY = Territory(17, "Guernsey")
  val AIA = Territory(18, "Anguilla")
  val MSR = Territory(19, "Montserrat")
  val JEY = Territory(20, "Jersey")
  val CXR = Territory(21, "Christmas Island", None, Seq("AU-CX", "AUS-CX"))
  val WLF = Territory(22, "Wallis and Futuna", None, Seq.empty, Seq("Futuna"))
  val VGB = Territory(23, "British Virgin Islands", None, Seq.empty, Seq("Virgin Islands, British"))
  val LIE = Territory(24, "Liechtenstein")
  val ABW = Territory(25, "Aruba")
  val MHL = Territory(26, "Marshall Islands", None, Seq("WAK"))
  val ASM = Territory(27, "American Samoa", None, Seq("US-AS", "USA-AS"), Seq("Samoa, American"))
  val COK = Territory(28, "Cook islands")
  val SPM = Territory(29, "Saint Pierre and Miquelon", None, Seq.empty, Seq("Miquelon"))
  val NIU = Territory(30, "Niue")
  val KNA = Territory(31, "Saint Kitts and Nevis", None, Seq.empty, Seq("Nevis"))
  val CYM = Territory(32, "Cayman islands")
  val BES = Territory(33, "Bonaire, St Eustasuis and Saba", None, Seq.empty, Seq("Saba", "St Eustasius"))
  val MDV = Territory(34, "Maldives")
  val SHN = Territory(35, "Saint Helena, Ascension and Tristan da Cunha", None, Seq("TAA", "ASC"), Seq("Ascension", "Tristan da Cunha"))
  val MLT = Territory(36, "Malta")
  val GRD = Territory(37, "Grenada")
  val VIR = Territory(38, "US Virgin Islands", None, Seq("US-VI", "USA-VI"), Seq("Virgin Islands, US"))
  val MYT = Territory(39, "Mayotte")
  val SJM = Territory(40, "Svalbard and Jan Mayen", None, Seq.empty, Seq("Jan Mayen"))
  val VCT = Territory(41, "Saint Vincent and the Grenadines", None, Seq.empty, Seq("Grenadines"))
  val HMD = Territory(42, "Heard Island and McDonald Islands", None, Seq("AU-HM", "AUS-HM"), Seq("McDonald Islands"))
  val BRB = Territory(43, "Barbados")
  val ATG = Territory(44, "Antigua and Barbuda", None, Seq.empty, Seq("Barbuda"))
  val CUW = Territory(45, "Curacao")
  val SYC = Territory(46, "Seychelles")
  val PLW = Territory(47, "Palau")
  val MNP = Territory(48, "Northern Mariana Islands", None, Seq("US-MP", "USA-MP"))
  val AND = Territory(49, "Andorra")
  val GUM = Territory(50, "Guam", None, Seq("US-GU", "USA-GU"))
  val IMN = Territory(51, "Isle of Man")
  val LCA = Territory(52, "Saint Lucia")
  val FSM = Territory(53, "Micronesia", None, Seq.empty, Seq("Federated States of Micronesia"))
  val SGP = Territory(54, "Singapore")
  val TON = Territory(55, "Tonga")
  val DMA = Territory(56, "Dominica")
  val BHR = Territory(57, "Bahrain")
  val KIR = Territory(58, "Kiribati")
  val TCA = Territory(59, "Turks and Caicos Islands", None, Seq.empty, Seq("Caicos Islands"))
  val STP = Territory(60, "Sao Tome and Principe", None, Seq.empty, Seq("Principe"))
  val HKG = Territory(61, "Hong Kong", None, Seq("CN-91", "CHN-91", "CN-HK", "CHN-HK"))
  val MTQ = Territory(62, "Martinique")
  val FRO = Territory(63, "Faroe Islands")
  val GLP = Territory(64, "Guadeloupe")
  val COM = Territory(65, "Comoros")
  val MUS = Territory(66, "Mauritius")
  val REU = Territory(67, "Reunion")
  val LUX = Territory(68, "Luxembourg")
  val WSM = Territory(69, "Samoa")
  val SGS = Territory(70, "South Georgia and the South Sandwich Islands", None, Seq.empty, Seq("South Sandwich Islands"))
  val PYF = Territory(71, "French Polynesia")
  val CPV = Territory(72, "Cape Verde")
  val TTO = Territory(73, "Trinidad and Tobago", None, Seq.empty, Seq("Tobago"))
  val BRN = Territory(74, "Brunei")
  val ATF = Territory(75, "French Southern and Antarctic Lands")
  val PRI = Territory(76, "Puerto Rico", None, Seq("US-PR", "USA-PR"))
  val CYP = Territory(77, "Cyprus")
  val LBN = Territory(78, "Lebanon")
  val JAM = Territory(79, "Jamaica")
  val GMB = Territory(80, "Gambia")
  val QAT = Territory(81, "Qatar")
  val FLK = Territory(82, "Falkland Islands")
  val VUT = Territory(83, "Vanuatu")
  val MNE = Territory(84, "Montenegro")
  val BHS = Territory(85, "Bahamas")
  val TLS = Territory(86, "East Timor")
  val SWZ = Territory(87, "Swaziland")
  val KWT = Territory(88, "Kuwait")
  val FJI = Territory(89, "Fiji Islands")
  val NCL = Territory(90, "New Caledonia")
  val SVN = Territory(91, "Slovenia")
  val ISR = Territory(92, "Israel")
  val PSE = Territory(93, "Palestinian territory")
  val SLV = Territory(94, "El Salvador")
  val BLZ = Territory(95, "Belize")
  val DJI = Territory(96, "Djibouti")
  val MKD = Territory(97, "Macedonia")
  val RWA = Territory(98, "Rwanda")
  val HTI = Territory(99, "Haiti")
  val BDI = Territory(100, "Burundi")
  val GNQ = Territory(101, "Equatorial Guinea")
  val ALB = Territory(102, "Albania")
  val SLB = Territory(103, "Solomon Islands")
  val ARM = Territory(104, "Armenia")
  val LSO = Territory(105, "Lesotho")
  val BEL = Territory(106, "Belgium")
  val MDA = Territory(107, "Moldova")
  val GNB = Territory(108, "Guinea-Bissau")
  val TWN = Territory(109, "Taiwan", None, Seq("CN-71", "CHN-71", "CN-TW", "CHN-TW"))
  val BTN = Territory(110, "Bhutan")
  val CHE = Territory(111, "Switzerland")
  val NLD = Territory(112, "Netherlands")
  val DNK = Territory(113, "Denmark")
  val EST = Territory(114, "Estonia")
  val DOM = Territory(115, "Dominican Republic")
  val SVK = Territory(116, "Slovakia")
  val CRI = Territory(117, "Costa Rica")
  val BIH = Territory(118, "Bosnia and Herzegovina")
  val HRV = Territory(119, "Croatia")
  val TGO = Territory(120, "Togo")
  val LVA = Territory(121, "Latvia")
  val LTU = Territory(122, "Lithuania")
  val LKA = Territory(123, "Sri Lanka")
  val GEO = Territory(124, "Georgia")
  val IRL = Territory(125, "Ireland")
  val SLE = Territory(126, "Sierra Leone")
  val PAN = Territory(127, "Panama")
  val CZE = Territory(128, "Czech Republic")
  val GUF = Territory(129, "French Guiana")
  val ARE = Territory(130, "United Arab Emirates")
  val AUT = Territory(131, "Austria")
  val AZE = Territory(132, "Azerbaijan")
  val SRB = Territory(133, "Serbia")
  val JOR = Territory(134, "Jordan")
  val PRT = Territory(135, "Portugal")
  val HUN = Territory(136, "Hungary")
  val KOR = Territory(137, "South Korea")
  val ISL = Territory(138, "Iceland")
  val GTM = Territory(139, "Guatemala")
  val CUB = Territory(140, "Cuba")
  val BGR = Territory(141, "Bulgaria")
  val LBR = Territory(142, "Liberia")
  val HND = Territory(143, "Honduras")
  val BEN = Territory(144, "Benin")
  val ERI = Territory(145, "Eritrea")
  val MWI = Territory(146, "Malawi")
  val PRK = Territory(147, "North Korea")
  val NIC = Territory(148, "Nicaragua")
  val GRC = Territory(149, "Greece")
  val TJK = Territory(150, "Tajikistan")
  val BGD = Territory(151, "Bangladesh")
  val NPL = Territory(152, "Nepal")
  val TUN = Territory(153, "Tunisia")
  val SUR = Territory(154, "Suriname")
  val URY = Territory(155, "Uruguay")
  val KHM = Territory(156, "Cambodia")
  val SYR = Territory(157, "Syria")
  val SEN = Territory(158, "Senegal")
  val KGZ = Territory(159, "Kyrgyzstan")
  val BLR = Territory(160, "Belarus")
  val GUY = Territory(161, "Guyana")
  val LAO = Territory(162, "Laos")
  val ROU = Territory(163, "Romania")
  val GHA = Territory(164, "Ghana")
  val UGA = Territory(165, "Uganda")
  val GBR = Territory(166, "United Kingdom", None, Seq.empty, Seq("Scotland", "Great Britain", "Northern Ireland", "Ireland, Northern"))
  val GIN = Territory(167, "Guinea")
  val ECU = Territory(168, "Ecuador")
  val ESH = Territory(169, "Western Sahara", None, Seq.empty, Seq("Sahrawi"))
  val GAB = Territory(170, "Gabon")
  val NZL = Territory(171, "New Zealand")
  val BFA = Territory(172, "Burkina Faso")
  val PHL = Territory(173, "Philippines")
  val ITA = Territory(174, "Italy")
  val OMN = Territory(175, "Oman")
  val POL = Territory(176, "Poland")
  val CIV = Territory(177, "Ivory Coast")
  val NOR = Territory(178, "Norway")
  val MYS = Territory(179, "Malaysia")
  val VNM = Territory(180, "Vietnam")
  val FIN = Territory(181, "Finland")
  val COG = Territory(182, "Congo-Brazzaville")
  val DEU = Territory(183, "Germany")
  val JPN = Territory(184, "Japan")
  val ZWE = Territory(185, "Zimbabwe")
  val PRY = Territory(186, "Paraguay")
  val IRQ = Territory(187, "Iraq")
  val MAR = Territory(188, "Morocco")
  val UZB = Territory(189, "Uzbekistan")
  val SWE = Territory(190, "Sweden")
  val PNG = Territory(191, "Papua New Guinea")
  val CMR = Territory(192, "Cameroon")
  val TKM = Territory(193, "Turkmenistan")
  val ESP = Territory(194, "Spain")
  val THA = Territory(195, "Thailand")
  val YEM = Territory(196, "Yemen")
  val FRA = Territory(197, "France")
  val ALA = Territory(198, "Aaland Islands")
  val KEN = Territory(199, "Kenya")
  val BWA = Territory(200, "Botswana")
  val MDG = Territory(201, "Madagascar")
  val UKR = Territory(202, "Ukraine")
  val SSD = Territory(203, "South Sudan")
  val CAF = Territory(204, "Central African Republic")
  val SOM = Territory(205, "Somalia")
  val AFG = Territory(206, "Afghanistan")
  val MMR = Territory(207, "Myanmar", None, Seq.empty, Seq("Burma"))
  val ZMB = Territory(208, "Zambia")
  val CHL = Territory(209, "Chile")
  val TUR = Territory(210, "Turkey")
  val PAK = Territory(211, "Pakistan")
  val MOZ = Territory(212, "Mozambique")
  val NAM = Territory(213, "Namibia")
  val VEN = Territory(214, "Venezuela")
  val NGA = Territory(215, "Nigeria")
  val TZA = Territory(216, "Tanzania", None, Seq("EAZ"))
  val EGY = Territory(217, "Egypt")
  val MRT = Territory(218, "Mauritania")
  val BOL = Territory(219, "Bolivia")
  val ETH = Territory(220, "Ethiopia")
  val COL = Territory(221, "Colombia")
  val ZAF = Territory(222, "South Africa")
  val MLI = Territory(223, "Mali")
  val AGO = Territory(224, "Angola")
  val NER = Territory(225, "Niger")
  val TCD = Territory(226, "Chad")
  val PER = Territory(227, "Peru")
  val MNG = Territory(228, "Mongolia")
  val IRN = Territory(229, "Iran")
  val LBY = Territory(230, "Libya")
  val SDN = Territory(231, "Sudan")
  val IDN = Territory(232, "Indonesia")
  val MX_DIF = Territory(233, "Federal District", Some(MEX), Seq("MX-DF"))
  val MX_TLA = Territory(234, "Tlaxcala", Some(MEX), Seq("MX-TL"))
  val MX_MOR = Territory(235, "Morelos", Some(MEX), Seq("MX-MO"))
  val MX_AGU = Territory(236, "Aguascalientes", Some(MEX), Seq("MX-AG"))
  val MX_CL = Territory(237, "Colima", Some(MEX), Seq("MX-COL"))
  val MX_QUE = Territory(238, "Queretaro", Some(MEX), Seq("MX-QE"))
  val MX_HID = Territory(239, "Hidalgo", Some(MEX), Seq("MX-HG"))
  val MX_MX = Territory(240, "Mexico State", Some(MEX), Seq("MX-ME", "MX-MEX"))
  val MX_TAB = Territory(241, "Tabasco", Some(MEX), Seq("MX-TB"))
  val MX_NAY = Territory(242, "Nayarit", Some(MEX), Seq("MX-NA"))
  val MX_GUA = Territory(243, "Guanajuato", Some(MEX), Seq("MX-GT"))
  val MX_PUE = Territory(244, "Puebla", Some(MEX), Seq("MX-PB"))
  val MX_YUC = Territory(245, "Yucatan", Some(MEX), Seq("MX-YU"))
  val MX_ROO = Territory(246, "Quintana Roo", Some(MEX), Seq("MX-QR"))
  val MX_SIN = Territory(247, "Sinaloa", Some(MEX), Seq("MX-SI"))
  val MX_CAM = Territory(248, "Campeche", Some(MEX), Seq("MX-CM"))
  val MX_MIC = Territory(249, "Michoacan", Some(MEX), Seq("MX-MI"))
  val MX_SLP = Territory(250, "San Luis Potosi", Some(MEX), Seq("MX-SL"))
  val MX_GRO = Territory(251, "Guerrero", Some(MEX), Seq("MX-GR"))
  val MX_NLE = Territory(252, "Nuevo Leon", Some(MEX), Seq("MX-NL"))
  val MX_BCN = Territory(253, "Baja California", Some(MEX), Seq("MX-BC"))
  val MX_VER = Territory(254, "Veracruz", Some(MEX), Seq("MX-VE"))
  val MX_CHP = Territory(255, "Chiapas", Some(MEX), Seq("MX-CS"))
  val MX_BCS = Territory(256, "Baja California Sur", Some(MEX), Seq("MX-BS"))
  val MX_ZAC = Territory(257, "Zacatecas", Some(MEX), Seq("MX-ZA"))
  val MX_JAL = Territory(258, "Jalisco", Some(MEX), Seq("MX-JA"))
  val MX_TAM = Territory(259, "Tamaulipas", Some(MEX), Seq("MX-TM"))
  val MX_OAX = Territory(260, "Oaxaca", Some(MEX), Seq("MX-OA"))
  val MX_DUR = Territory(261, "Durango", Some(MEX), Seq("MX-DG"))
  val MX_COA = Territory(262, "Coahuila", Some(MEX), Seq("MX-CO"))
  val MX_SON = Territory(263, "Sonora", Some(MEX), Seq("MX-SO"))
  val MX_CHH = Territory(264, "Chihuahua", Some(MEX), Seq("MX-CH"))
  val GRL = Territory(265, "Greenland")
  val SAU = Territory(266, "Saudi Arabia")
  val COD = Territory(267, "Congo-Kinshasa")
  val DZA = Territory(268, "Algeria")
  val KAZ = Territory(269, "Kazakhstan")
  val ARG = Territory(270, "Argentina")
  val IN_DD = Territory(271, "Daman and Diu", Some(IND))
  val IN_DN = Territory(272, "Dadra and Nagar Haveli", Some(IND))
  val IN_CH = Territory(273, "Chandigarh", Some(IND))
  val IN_AN = Territory(274, "Andaman and Nicobar", Some(IND))
  val IN_LD = Territory(275, "Lakshadweep", Some(IND))
  val IN_DL = Territory(276, "Delhi", Some(IND))
  val IN_ML = Territory(277, "Meghalaya", Some(IND))
  val IN_NL = Territory(278, "Nagaland", Some(IND))
  val IN_MN = Territory(279, "Manipur", Some(IND))
  val IN_TR = Territory(280, "Tripura", Some(IND))
  val IN_MZ = Territory(281, "Mizoram", Some(IND))
  val IN_SK = Territory(282, "Sikkim", Some(IND), Seq("IN-SKM"))
  val IN_PB = Territory(283, "Punjab", Some(IND))
  val IN_HR = Territory(284, "Haryana", Some(IND))
  val IN_AR = Territory(285, "Arunachal Pradesh", Some(IND))
  val IN_AS = Territory(286, "Assam", Some(IND))
  val IN_BR = Territory(287, "Bihar", Some(IND))
  val IN_UT = Territory(288, "Uttarakhand", Some(IND), Seq("IN-UK"))
  val IN_GA = Territory(289, "Goa", Some(IND))
  val IN_KL = Territory(290, "Kerala", Some(IND))
  val IN_TN = Territory(291, "Tamil Nuda", Some(IND))
  val IN_HP = Territory(292, "Himachal Pradesh", Some(IND))
  val IN_JK = Territory(293, "Jammu and Kashmir", Some(IND))
  val IN_CT = Territory(294, "Chhattisgarh", Some(IND), Seq("IN-CG"))
  val IN_JH = Territory(295, "Jharkhand", Some(IND))
  val IN_KA = Territory(296, "Karnataka", Some(IND))
  val IN_RJ = Territory(297, "Rajasthan", Some(IND))
  val IN_OR = Territory(298, "Odisha", Some(IND), Seq("IN-OD"), Seq("Orissa"))
  val IN_GJ = Territory(299, "Gujarat", Some(IND))
  val IN_WB = Territory(300, "West Bengal", Some(IND))
  val IN_MP = Territory(301, "Madhya Pradesh", Some(IND))
  val IN_AP = Territory(302, "Andhra Pradesh", Some(IND))
  val IN_MH = Territory(303, "Maharashtra", Some(IND))
  val IN_UP = Territory(304, "Uttar Pradesh", Some(IND))
  val IN_PY = Territory(305, "Puducherry", Some(IND))
  val AU_NSW = Territory(306, "New South Wales", Some(AUS))
  val AU_ACT = Territory(307, "Australian Capital Territory", Some(AUS))
  val AU_JBT = Territory(308, "Jervis Bay Territory", Some(AUS), Seq("AU-JB"))
  val AU_NT = Territory(309, "Northern Territory", Some(AUS))
  val AU_SA = Territory(310, "South Australia", Some(AUS))
  val AU_TAS = Territory(311, "Tasmania", Some(AUS), Seq("AU-TS"))
  val AU_VIC = Territory(312, "Victoria", Some(AUS))
  val AU_WA = Territory(313, "Western Australia", Some(AUS))
  val AU_QLD = Territory(314, "Queensland", Some(AUS), Seq("AU-QL"))
  val BR_DF = Territory(315, "Distrito Federal", Some(BRA))
  val BR_SE = Territory(316, "Sergipe", Some(BRA))
  val BR_AL = Territory(317, "Alagoas", Some(BRA))
  val BR_RJ = Territory(318, "Rio de Janeiro", Some(BRA))
  val BR_ES = Territory(319, "Espirito Santo", Some(BRA))
  val BR_RN = Territory(320, "Rio Grande do Norte", Some(BRA))
  val BR_PB = Territory(321, "Paraiba", Some(BRA))
  val BR_SC = Territory(322, "Santa Catarina", Some(BRA))
  val BR_PE = Territory(323, "Pernambuco", Some(BRA))
  val BR_AP = Territory(324, "Amapa", Some(BRA))
  val BR_CE = Territory(325, "Ceara", Some(BRA))
  val BR_AC = Territory(326, "Acre", Some(BRA))
  val BR_PR = Territory(327, "Parana", Some(BRA))
  val BR_RR = Territory(328, "Roraima", Some(BRA))
  val BR_RO = Territory(329, "Rondonia", Some(BRA))
  val BR_SP = Territory(330, "Sao Paulo", Some(BRA))
  val BR_PI = Territory(331, "Piaui", Some(BRA))
  val BR_TO = Territory(332, "Tocantins", Some(BRA))
  val BR_RS = Territory(333, "Rio Grande do Sul", Some(BRA))
  val BR_MA = Territory(334, "Maranhao", Some(BRA))
  val BR_GO = Territory(335, "Goias", Some(BRA))
  val BR_MS = Territory(336, "Mato Grosso do Sul", Some(BRA))
  val BR_BA = Territory(337, "Bahia", Some(BRA))
  val BR_MG = Territory(338, "Minas Gerais", Some(BRA))
  val BR_MT = Territory(339, "Mato Grosso", Some(BRA))
  val BR_PA = Territory(340, "Para", Some(BRA))
  val BR_AM = Territory(341, "Amazonas", Some(BRA))
  val US_DC = Territory(342, "District of Columbia", Some(USA))
  val US_RI = Territory(343, "Rhode Island", Some(USA))
  val US_DE = Territory(344, "Delaware", Some(USA))
  val US_CT = Territory(345, "Connecticut", Some(USA))
  val US_NJ = Territory(346, "New Jersey", Some(USA))
  val US_NH = Territory(347, "New Hampshire", Some(USA))
  val US_VT = Territory(348, "Vermont", Some(USA))
  val US_MA = Territory(349, "Massachusetts", Some(USA))
  val US_HI = Territory(350, "Hawaii", Some(USA), Seq("US-MID"))
  val US_MD = Territory(351, "Maryland", Some(USA))
  val US_WV = Territory(352, "West Virginia", Some(USA))
  val US_SC = Territory(353, "South Carolina", Some(USA))
  val US_ME = Territory(354, "Maine", Some(USA))
  val US_IN = Territory(355, "Indiana", Some(USA))
  val US_KY = Territory(356, "Kentucky", Some(USA))
  val US_TN = Territory(357, "Tennessee", Some(USA))
  val US_VA = Territory(358, "Virginia", Some(USA))
  val US_OH = Territory(359, "Ohio", Some(USA))
  val US_PA = Territory(360, "Pennsylvania", Some(USA))
  val US_MS = Territory(361, "Mississippi", Some(USA))
  val US_LA = Territory(362, "Louisiana", Some(USA))
  val US_AL = Territory(363, "Alabama", Some(USA))
  val US_AR = Territory(364, "Arkansas", Some(USA))
  val US_NC = Territory(365, "North Carolina", Some(USA))
  val US_NY = Territory(366, "New York", Some(USA))
  val US_IA = Territory(367, "Iowa", Some(USA))
  val US_IL = Territory(368, "Illinois", Some(USA))
  val US_GA = Territory(369, "Georgia", Some(USA))
  val US_WI = Territory(370, "Wisconsin", Some(USA))
  val US_FL = Territory(371, "Florida", Some(USA))
  val US_MO = Territory(372, "Missouri", Some(USA))
  val US_OK = Territory(373, "Oklahoma", Some(USA))
  val US_ND = Territory(374, "North Dakota", Some(USA))
  val US_WA = Territory(375, "Washington", Some(USA))
  val US_SD = Territory(376, "South Dakota", Some(USA))
  val US_NE = Territory(377, "Nebraska", Some(USA))
  val US_KS = Territory(378, "Kansas", Some(USA))
  val US_ID = Territory(379, "Idaho", Some(USA))
  val US_UT = Territory(380, "Utah", Some(USA))
  val US_MN = Territory(381, "Minnesota", Some(USA))
  val US_MI = Territory(382, "Michigan", Some(USA))
  val US_WY = Territory(383, "Wyoming", Some(USA))
  val US_OR = Territory(384, "Oregon", Some(USA))
  val US_CO = Territory(385, "Colorado", Some(USA))
  val US_NV = Territory(386, "Nevada", Some(USA))
  val US_AZ = Territory(387, "Arizona", Some(USA))
  val US_NM = Territory(388, "New Mexico", Some(USA))
  val US_MT = Territory(389, "Montana", Some(USA))
  val US_CA = Territory(390, "California", Some(USA))
  val US_TX = Territory(391, "Texas", Some(USA))
  val US_AK = Territory(392, "Alaska", Some(USA))
  val CA_BC = Territory(393, "British Columbia", Some(CAN))
  val CA_AB = Territory(394, "Alberta", Some(CAN))
  val CA_ON = Territory(395, "Ontario", Some(CAN))
  val CA_QC = Territory(396, "Quebec", Some(CAN))
  val CA_SK = Territory(397, "Saskatchewan", Some(CAN))
  val CA_MB = Territory(398, "Manitoba", Some(CAN))
  val CA_NL = Territory(399, "Newfoundland", Some(CAN))
  val CA_NB = Territory(400, "New Brunswick", Some(CAN))
  val CA_NS = Territory(401, "Nova Scotia", Some(CAN))
  val CA_PE = Territory(402, "Prince Edward Island", Some(CAN))
  val CA_YT = Territory(403, "Yukon", Some(CAN))
  val CA_NT = Territory(404, "Northwest Territories", Some(CAN))
  val CA_NU = Territory(405, "Nunavut", Some(CAN))
  val RU_MOW = Territory(411, "Moscow", Some(RUS))
  val RU_SPE = Territory(412, "Saint Petersburg", Some(RUS))
  val RU_KGD = Territory(413, "Kaliningrad Oblast", Some(RUS))
  val RU_IN = Territory(414, "Ingushetia Republic", Some(RUS))
  val RU_AD = Territory(415, "Adygea Republic", Some(RUS))
  val RU_SE = Territory(416, "North Ossetia-Alania Republic", Some(RUS))
  val RU_KB = Territory(417, "Kabardino-Balkar Republic", Some(RUS))
  val RU_KC = Territory(418, "Karachay-Cherkess Republic", Some(RUS))
  val RU_CE = Territory(419, "Chechen Republic", Some(RUS))
  val RU_CU = Territory(420, "Chuvash Republic", Some(RUS))
  val RU_IVA = Territory(421, "Ivanovo Oblast", Some(RUS))
  val RU_LIP = Territory(422, "Lipetsk Oblast", Some(RUS))
  val RU_ORL = Territory(423, "Oryol Oblast", Some(RUS))
  val RU_TUL = Territory(424, "Tula Oblast", Some(RUS))
  val RU_BE = Territory(425, "Belgorod Oblast", Some(RUS), Seq("RU-BEL"))
  val RU_VLA = Territory(426, "Vladimir Oblast", Some(RUS))
  val RU_KRS = Territory(427, "Kursk Oblast", Some(RUS))
  val RU_KLU = Territory(428, "Kaluga Oblast", Some(RUS))
  val RU_TT = Territory(429, "Tambov Oblast", Some(RUS), Seq("RU-TAM"))
  val RU_BRY = Territory(430, "Bryansk Oblast", Some(RUS))
  val RU_YAR = Territory(431, "Yaroslavl Oblast", Some(RUS))
  val RU_RYA = Territory(432, "Ryazan Oblast", Some(RUS))
  val RU_AST = Territory(433, "Astrakhan Oblast", Some(RUS))
  val RU_MOS = Territory(434, "Moscow Oblast", Some(RUS))
  val RU_SMO = Territory(435, "Smolensk Oblast", Some(RUS))
  val RU_DA = Territory(436, "Dagestan Republic", Some(RUS))
  val RU_VOR = Territory(437, "Voronezh Oblast", Some(RUS))
  val RU_NGR = Territory(438, "Novgorod Oblast", Some(RUS))
  val RU_PSK = Territory(439, "Pskov Oblast", Some(RUS))
  val RU_KOS = Territory(440, "Kostroma Oblast", Some(RUS))
  val RU_STA = Territory(441, "Stavropol Krai", Some(RUS))
  val RU_KDA = Territory(442, "Krasnodar Krai", Some(RUS))
  val RU_KL = Territory(443, "Kalmykia Republic", Some(RUS))
  val RU_TVE = Territory(444, "Tver Oblast", Some(RUS))
  val RU_LEN = Territory(445, "Leningrad Oblast", Some(RUS))
  val RU_ROS = Territory(446, "Rostov Oblast", Some(RUS))
  val RU_VGG = Territory(447, "Volgograd Oblast", Some(RUS))
  val RU_VLG = Territory(448, "Vologda Oblast", Some(RUS))
  val RU_MUR = Territory(449, "Murmansk Oblast", Some(RUS))
  val RU_KR = Territory(450, "Karelia Republic", Some(RUS))
  val RU_NEN = Territory(451, "Nenets Autonomous Okrug", Some(RUS))
  val RU_KO = Territory(452, "Komi Republic", Some(RUS))
  val RU_ARK = Territory(453, "Arkhangelsk Oblast", Some(RUS))
  val RU_MO = Territory(454, "Mordovia Republic", Some(RUS))
  val RU_NIZ = Territory(455, "Nizhny Novgorod Oblast", Some(RUS))
  val RU_PNZ = Territory(456, "Penza Oblast", Some(RUS))
  val RU_KI = Territory(457, "Kirov Oblast", Some(RUS), Seq("RU-KIR"))
  val RU_ME = Territory(458, "Mari El Republic", Some(RUS))
  val RU_ORE = Territory(459, "Orenburg Oblast", Some(RUS))
  val RU_ULY = Territory(460, "Ulyanovsk Oblast", Some(RUS))
  val RU_PM = Territory(461, "Perm Krai", Some(RUS), Seq("RU-PER"))
  val RU_BA = Territory(462, "Bashkortostan Republic", Some(RUS))
  val RU_UD = Territory(463, "Udmurt Republic", Some(RUS))
  val RU_TA = Territory(464, "Tatarstan Republic", Some(RUS))
  val RU_SAM = Territory(465, "Samara Oblast", Some(RUS))
  val RU_SAR = Territory(466, "Saratov Oblast", Some(RUS))
  val RU_YAN = Territory(467, "Yamalo-Nenets", Some(RUS))
  val RU_KM = Territory(468, "Khanty-Mansi", Some(RUS), Seq("RU-KHM"))
  val RU_SVE = Territory(469, "Sverdlovsk Oblast", Some(RUS))
  val RU_TYU = Territory(470, "Tyumen Oblast", Some(RUS))
  val RU_KGN = Territory(471, "Kurgan Oblast", Some(RUS))
  val RU_CH = Territory(472, "Chelyabinsk Oblast", Some(RUS), Seq("RU-CHE"))
  val RU_BU = Territory(473, "Buryatia Republic", Some(RUS))
  val RU_ZAB = Territory(474, "Zabaykalsky Krai", Some(RUS))
  val RU_IRK = Territory(475, "Irkutsk Oblast", Some(RUS))
  val RU_NVS = Territory(476, "Novosibirsk Oblast", Some(RUS))
  val RU_TOM = Territory(477, "Tomsk Oblast", Some(RUS))
  val RU_OMS = Territory(478, "Omsk Oblast", Some(RUS))
  val RU_KK = Territory(479, "Khakassia Republic", Some(RUS))
  val RU_KEM = Territory(480, "Kemerovo Oblast", Some(RUS))
  val RU_AL = Territory(481, "Altai Republic", Some(RUS))
  val RU_ALT = Territory(482, "Altai Krai", Some(RUS))
  val RU_TY = Territory(483, "Tuva Republic", Some(RUS))
  val RU_KYA = Territory(484, "Krasnoyarsk Krai", Some(RUS))
  val RU_MAG = Territory(485, "Magadan Oblast", Some(RUS))
  val RU_CHU = Territory(486, "Chukotka Okrug", Some(RUS))
  val RU_KAM = Territory(487, "Kamchatka Krai", Some(RUS))
  val RU_SAK = Territory(488, "Sakhalin Oblast", Some(RUS))
  val RU_PO = Territory(489, "Primorsky Krai", Some(RUS), Seq("RU-PRI"))
  val RU_YEV = Territory(490, "Jewish Autonomous Oblast", Some(RUS))
  val RU_KHA = Territory(491, "Khabarovsk Krai", Some(RUS))
  val RU_AMU = Territory(492, "Amur Oblast", Some(RUS))
  val RU_SA = Territory(493, "Sakha Republic", Some(RUS), Seq.empty, Seq("Yakutia Republic"))
  val CN_SH = Territory(496, "Shanghai", Some(CHN), Seq("CN-31"))
  val CN_TJ = Territory(497, "Tianjin", Some(CHN), Seq("CN-12"))
  val CN_BJ = Territory(498, "Beijing", Some(CHN), Seq("CN-11"))
  val CN_HI = Territory(499, "Hainan", Some(CHN), Seq("CN-46"))
  val CN_NX = Territory(500, "Ningxia Hui", Some(CHN), Seq("CN-64"))
  val CN_CQ = Territory(501, "Chongqing", Some(CHN), Seq("CN-50"))
  val CN_ZJ = Territory(502, "Zhejiang", Some(CHN), Seq("CN-33"))
  val CN_JS = Territory(503, "Jiangsu", Some(CHN), Seq("CN-32"))
  val CN_FJ = Territory(504, "Fujian", Some(CHN), Seq("CN-35"))
  val CN_AH = Territory(505, "Anhui", Some(CHN), Seq("CN-34"))
  val CN_LN = Territory(506, "Liaoning", Some(CHN), Seq("CN-21"))
  val CN_SD = Territory(507, "Shandong", Some(CHN), Seq("CN-37"))
  val CN_SX = Territory(508, "Shanxi", Some(CHN), Seq("CN-14"))
  val CN_JX = Territory(509, "Jiangxi", Some(CHN), Seq("CN-36"))
  val CN_HA = Territory(510, "Henan", Some(CHN), Seq("CN-41"))
  val CN_GZ = Territory(511, "Guizhou", Some(CHN), Seq("CN-52"))
  val CN_GD = Territory(512, "Guangdong", Some(CHN), Seq("CN-44"))
  val CN_HB = Territory(513, "Hubei", Some(CHN), Seq("CN-42"))
  val CN_JL = Territory(514, "Jilin", Some(CHN), Seq("CN-22"))
  val CN_HE = Territory(515, "Hebei", Some(CHN), Seq("CN-13"))
  val CN_SN = Territory(516, "Shaanxi", Some(CHN), Seq("CN-61"))
  val CN_NM = Territory(517, "Nei Mongol", Some(CHN), Seq("CN-15"), Seq("Inner Mongolia"))
  val CN_HL = Territory(518, "Heilongjiang", Some(CHN), Seq("CN-23"))
  val CN_HN = Territory(519, "Hunan", Some(CHN), Seq("CN-43"))
  val CN_GX = Territory(520, "Guangxi Zhuang", Some(CHN), Seq("CN-45"))
  val CN_SC = Territory(521, "Sichuan", Some(CHN), Seq("CN-51"))
  val CN_YN = Territory(522, "Yunnan", Some(CHN), Seq("CN-53"))
  val CN_XZ = Territory(523, "Xizang", Some(CHN), Seq("CN-54"), Seq("Tibet"))
  val CN_GS = Territory(524, "Gansu", Some(CHN), Seq("CN-62"))
  val CN_QH = Territory(525, "Qinghai", Some(CHN), Seq("CN-63"))
  val CN_XJ = Territory(526, "Xinjiang Uyghur", Some(CHN), Seq("CN-65"))
  val UMI = Territory(528, "United States Minor Outlying Islands", None, Seq("US-UM", "USA-UM", "JTN"))
  val CPT = Territory(529, "Clipperton Island")
  val AT0 = Territory(530, "Macquarie Island", Some(ATA))
  val AT1 = Territory(531, "Ross Dependency", Some(ATA))
  val AT2 = Territory(532, "Adelie Land", Some(ATA))
  val AT3 = Territory(533, "Australian Antarctic Territory", Some(ATA))
  val AT4 = Territory(534, "Queen Maud Land", Some(ATA))
  val AT5 = Territory(535, "British Antarctic Territory", Some(ATA))
  val AT6 = Territory(536, "Chile Antartica", Some(ATA))
  val AT7 = Territory(537, "Argentine Antarctica", Some(ATA))
  val AT8 = Territory(538, "Peter 1 Island", Some(ATA))
  val AAA = Territory(540, "International", None, Seq.empty, Seq("Worldwide", "Earth"))
}

