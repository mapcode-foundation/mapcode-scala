package com.mapcode.scala

object TerritoryFixture extends TerritoryOperations {
  val USA = Territory(409, "USA", None, Seq("US"), Seq("United States of America", "America"))
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
}
