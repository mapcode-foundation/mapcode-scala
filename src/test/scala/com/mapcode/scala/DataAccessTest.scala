package com.mapcode.scala

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class DataAccessTest extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("asUnsignedByte") {
    DataAccess.asUnsignedByte(150664) should equal(118)
    DataAccess.asUnsignedByte(12533) should equal(72)
    DataAccess.asUnsignedByte(28079) should equal(0)
    DataAccess.asUnsignedByte(302462) should equal(169)
    DataAccess.asUnsignedByte(313613) should equal(92)
    DataAccess.asUnsignedByte(88242) should equal(149)
    DataAccess.asUnsignedByte(128981) should equal(64)
    DataAccess.asUnsignedByte(71844) should equal(192)
    DataAccess.asUnsignedByte(188858) should equal(160)
    DataAccess.asUnsignedByte(188543) should equal(250)
    DataAccess.asUnsignedByte(111164) should equal(36)
    DataAccess.asUnsignedByte(88198) should equal(1)
    DataAccess.asUnsignedByte(183545) should equal(82)
    DataAccess.asUnsignedByte(146552) should equal(103)
    DataAccess.asUnsignedByte(313291) should equal(7)
    DataAccess.asUnsignedByte(315409) should equal(81)
    DataAccess.asUnsignedByte(253787) should equal(1)
    DataAccess.asUnsignedByte(172024) should equal(164)
    DataAccess.asUnsignedByte(2359) should equal(0)
    DataAccess.asUnsignedByte(139000) should equal(170)
    DataAccess.asUnsignedByte(175343) should equal(0)
    DataAccess.asUnsignedByte(129011) should equal(255)
    DataAccess.asUnsignedByte(311707) should equal(2)
    DataAccess.asUnsignedByte(226801) should equal(251)
    DataAccess.asUnsignedByte(312074) should equal(40)
    DataAccess.asUnsignedByte(192461) should equal(103)
    DataAccess.asUnsignedByte(221951) should equal(5)
    DataAccess.asUnsignedByte(84929) should equal(142)
    DataAccess.asUnsignedByte(199592) should equal(112)
    DataAccess.asUnsignedByte(318672) should equal(155)
    DataAccess.asUnsignedByte(293237) should equal(6)
    DataAccess.asUnsignedByte(131113) should equal(80)
    DataAccess.asUnsignedByte(241816) should equal(54)
    DataAccess.asUnsignedByte(54181) should equal(125)
    DataAccess.asUnsignedByte(169721) should equal(151)
    DataAccess.asUnsignedByte(179298) should equal(156)
    DataAccess.asUnsignedByte(38249) should equal(73)
    DataAccess.asUnsignedByte(194874) should equal(225)
    DataAccess.asUnsignedByte(128104) should equal(88)
    DataAccess.asUnsignedByte(95415) should equal(1)
    DataAccess.asUnsignedByte(221660) should equal(244)
    DataAccess.asUnsignedByte(112929) should equal(56)
    DataAccess.asUnsignedByte(308580) should equal(232)
    DataAccess.asUnsignedByte(151484) should equal(100)
    DataAccess.asUnsignedByte(50655) should equal(2)
    DataAccess.asUnsignedByte(104656) should equal(177)
    DataAccess.asUnsignedByte(306804) should equal(83)
    DataAccess.asUnsignedByte(322167) should equal(255)
    DataAccess.asUnsignedByte(165040) should equal(255)
    DataAccess.asUnsignedByte(9394) should equal(120)
    DataAccess.asUnsignedByte(7761) should equal(2)
    DataAccess.asUnsignedByte(226637) should equal(0)
    DataAccess.asUnsignedByte(97748) should equal(6)
    DataAccess.asUnsignedByte(276022) should equal(31)
    DataAccess.asUnsignedByte(16172) should equal(165)
    DataAccess.asUnsignedByte(294975) should equal(3)
    DataAccess.asUnsignedByte(70660) should equal(172)
    DataAccess.asUnsignedByte(155240) should equal(13)
    DataAccess.asUnsignedByte(310986) should equal(125)
    DataAccess.asUnsignedByte(313981) should equal(65)
    DataAccess.asUnsignedByte(74219) should equal(3)
    DataAccess.asUnsignedByte(64393) should equal(72)
    DataAccess.asUnsignedByte(227200) should equal(155)
    DataAccess.asUnsignedByte(137987) should equal(0)
    DataAccess.asUnsignedByte(17965) should equal(211)
    DataAccess.asUnsignedByte(217641) should equal(189)
    DataAccess.asUnsignedByte(118149) should equal(133)
    DataAccess.asUnsignedByte(62717) should equal(24)
    DataAccess.asUnsignedByte(164387) should equal(0)
    DataAccess.asUnsignedByte(254618) should equal(177)
    DataAccess.asUnsignedByte(213082) should equal(89)
    DataAccess.asUnsignedByte(11759) should equal(3)
    DataAccess.asUnsignedByte(15654) should equal(226)
    DataAccess.asUnsignedByte(275376) should equal(140)
    DataAccess.asUnsignedByte(148810) should equal(130)
    DataAccess.asUnsignedByte(12937) should equal(0)
    DataAccess.asUnsignedByte(313862) should equal(189)
    DataAccess.asUnsignedByte(293) should equal(68)
    DataAccess.asUnsignedByte(262033) should equal(112)
    DataAccess.asUnsignedByte(208061) should equal(187)
    DataAccess.asUnsignedByte(222521) should equal(61)
    DataAccess.asUnsignedByte(3056) should equal(139)
    DataAccess.asUnsignedByte(83434) should equal(93)
    DataAccess.asUnsignedByte(79826) should equal(114)
    DataAccess.asUnsignedByte(316906) should equal(54)
    DataAccess.asUnsignedByte(235933) should equal(189)
    DataAccess.asUnsignedByte(239890) should equal(52)
    DataAccess.asUnsignedByte(71588) should equal(251)
    DataAccess.asUnsignedByte(308455) should equal(2)
    DataAccess.asUnsignedByte(86042) should equal(73)
    DataAccess.asUnsignedByte(75158) should equal(1)
    DataAccess.asUnsignedByte(151030) should equal(184)
    DataAccess.asUnsignedByte(77528) should equal(217)
    DataAccess.asUnsignedByte(321805) should equal(185)
    DataAccess.asUnsignedByte(149904) should equal(32)
    DataAccess.asUnsignedByte(272060) should equal(51)
    DataAccess.asUnsignedByte(211602) should equal(120)
    DataAccess.asUnsignedByte(141815) should equal(255)
    DataAccess.asUnsignedByte(254470) should equal(171)
    DataAccess.asUnsignedByte(118200) should equal(207)

    an[IllegalArgumentException] should be thrownBy DataAccess.asUnsignedByte(-1)
    an[IllegalArgumentException] should be thrownBy DataAccess.asUnsignedByte(DataAccess.FILE_DATA.length)
  }

  test("dataFlags") {
    an[IllegalArgumentException] should be thrownBy DataAccess.dataFlags(-1)
    an[IllegalArgumentException] should be thrownBy DataAccess.dataFlags(DataAccess.FILE_DATA.length)
    DataAccess.dataFlags(9908) should equal(45233)
    DataAccess.dataFlags(6432) should equal(1099)
    DataAccess.dataFlags(0) should equal(43)
    DataAccess.dataFlags(11523) should equal(75)
    DataAccess.dataFlags(1) should equal(1590)
    DataAccess.dataFlags(7967) should equal(75)
    DataAccess.dataFlags(9999) should equal(144)
    DataAccess.dataFlags(2001) should equal(75)
    DataAccess.dataFlags(14571) should equal(75)
    DataAccess.dataFlags(5529) should equal(54)
    DataAccess.dataFlags(14687) should equal(75)
    DataAccess.dataFlags(11849) should equal(75)
    DataAccess.dataFlags(983) should equal(75)
    DataAccess.dataFlags(9602) should equal(12)
    DataAccess.dataFlags(3214) should equal(75)
    DataAccess.dataFlags(7120) should equal(75)
    DataAccess.dataFlags(11692) should equal(3249)
    DataAccess.dataFlags(5543) should equal(1099)
    DataAccess.dataFlags(12906) should equal(28844)
    DataAccess.dataFlags(14595) should equal(71)
    DataAccess.dataFlags(2097) should equal(1098)
    DataAccess.dataFlags(8210) should equal(14480)
    DataAccess.dataFlags(187) should equal(10)
    DataAccess.dataFlags(2254) should equal(51372)
    DataAccess.dataFlags(1109) should equal(75)
    DataAccess.dataFlags(965) should equal(75)
    DataAccess.dataFlags(14366) should equal(75)
    DataAccess.dataFlags(10560) should equal(75)
    DataAccess.dataFlags(8730) should equal(14480)
    DataAccess.dataFlags(4520) should equal(75)
    DataAccess.dataFlags(14494) should equal(1590)
    DataAccess.dataFlags(12884) should equal(1591)
    DataAccess.dataFlags(10848) should equal(75)
  }

  test("asLong") {
    an [IllegalArgumentException] should be thrownBy DataAccess.asLong(-1)
    an [IllegalArgumentException] should be thrownBy DataAccess.asLong(DataAccess.FILE_DATA.length)

    DataAccess.asLong(65433) should equal(1258453938)
    DataAccess.asLong(134103) should equal(-1655799296)
    DataAccess.asLong(16960) should equal(8751000)
    DataAccess.asLong(18267) should equal(738074114)
    DataAccess.asLong(154883) should equal(-1542400773)
    DataAccess.asLong(183771) should equal(-194724607)
    DataAccess.asLong(2103) should equal(-2129458422)
    DataAccess.asLong(47741) should equal(1006677249)
    DataAccess.asLong(263445) should equal(1057122322)
    DataAccess.asLong(164207) should equal(1644109568)
    DataAccess.asLong(158886) should equal(1240007017)
    DataAccess.asLong(204751) should equal(-1435240444)
    DataAccess.asLong(258020) should equal(-94045119)
    DataAccess.asLong(98048) should equal(26301443)
    DataAccess.asLong(190713) should equal(-1409204894)
    DataAccess.asLong(296078) should equal(-1419311230)
    DataAccess.asLong(196277) should equal(134643712)
    DataAccess.asLong(197642) should equal(-1327758608)
    DataAccess.asLong(314475) should equal(-2147202303)
    DataAccess.asLong(196623) should equal(1955733506)
    DataAccess.asLong(971) should equal(-1838362620)
    DataAccess.asLong(204911) should equal(567808508)
    DataAccess.asLong(210286) should equal(640680285)
    DataAccess.asLong(194523) should equal(-1673367303)
    DataAccess.asLong(39800) should equal(40006000)
    DataAccess.asLong(243231) should equal(-84993027)
    DataAccess.asLong(78432) should equal(14102024)
    DataAccess.asLong(95766) should equal(-1933508334)
    DataAccess.asLong(135690) should equal(1555628947)
    DataAccess.asLong(208036) should equal(86189)
    DataAccess.asLong(239256) should equal(1704972)
    DataAccess.asLong(318567) should equal(-1513650943)
    DataAccess.asLong(164927) should equal(-988049153)
    DataAccess.asLong(180546) should equal(1007944385)
    DataAccess.asLong(220093) should equal(-1409192922)
    DataAccess.asLong(282323) should equal(-1773195016)
    DataAccess.asLong(13561) should equal(1375868545)
    DataAccess.asLong(54020) should equal(34870000)
    DataAccess.asLong(126897) should equal(939770112)
    DataAccess.asLong(260207) should equal(1341645058)
    DataAccess.asLong(129027) should equal(2030715137)
    DataAccess.asLong(110162) should equal(-2038429840)
    DataAccess.asLong(24470) should equal(1416429640)
    DataAccess.asLong(85779) should equal(1326052864)
    DataAccess.asLong(271010) should equal(1227225265)
    DataAccess.asLong(182687) should equal(1449973761)
    DataAccess.asLong(7580) should equal(42662127)
    DataAccess.asLong(6326) should equal(1231093452)
    DataAccess.asLong(194230) should equal(-913049089)
    DataAccess.asLong(148262) should equal(1308033672)
    DataAccess.asLong(201166) should equal(-1295188050)
    DataAccess.asLong(67830) should equal(-1856438406)
    DataAccess.asLong(224279) should equal(1656472320)
    DataAccess.asLong(103369) should equal(1476083002)
    DataAccess.asLong(284698) should equal(17235969)
    DataAccess.asLong(42008) should equal(-82366536)
    DataAccess.asLong(301409) should equal(990300999)
    DataAccess.asLong(103166) should equal(1309081783)
    DataAccess.asLong(128556) should equal(62981195)
    DataAccess.asLong(180781) should equal(1879447076)
    DataAccess.asLong(305525) should equal(1006898369)
    DataAccess.asLong(292895) should equal(-888917757)
    DataAccess.asLong(318113) should equal(1258475861)
    DataAccess.asLong(284176) should equal(11024561)
    DataAccess.asLong(249217) should equal(-1744829984)
    DataAccess.asLong(14014) should equal(4915404)
    DataAccess.asLong(260658) should equal(1947598876)
    DataAccess.asLong(142762) should equal(1610220078)
    DataAccess.asLong(162584) should equal(10196000)
    DataAccess.asLong(134870) should equal(2046296299)
    DataAccess.asLong(23) should equal(485265408)
    DataAccess.asLong(192111) should equal(256566777)
    DataAccess.asLong(38442) should equal(-1147140919)
    DataAccess.asLong(93383) should equal(1954989572)
    DataAccess.asLong(193394) should equal(68223332)
    DataAccess.asLong(126001) should equal(738319693)
    DataAccess.asLong(2600) should equal(-165960000)
    DataAccess.asLong(254220) should equal(-89844032)
    DataAccess.asLong(220896) should equal(62980171)
    DataAccess.asLong(69577) should equal(620757432)
    DataAccess.asLong(201418) should equal(-1017118696)
    DataAccess.asLong(229410) should equal(2048394374)
    DataAccess.asLong(183280) should equal(56619805)
    DataAccess.asLong(95669) should equal(-872005087)
    DataAccess.asLong(54841) should equal(-1761641888)
    DataAccess.asLong(154629) should equal(-604264757)
    DataAccess.asLong(221063) should equal(-1179447292)
    DataAccess.asLong(299850) should equal(-587725928)
    DataAccess.asLong(265273) should equal(167940450)
    DataAccess.asLong(284446) should equal(-1292107455)
    DataAccess.asLong(187448) should equal(141037987)
    DataAccess.asLong(150319) should equal(-519566078)
    DataAccess.asLong(247593) should equal(-1409108428)
    DataAccess.asLong(106581) should equal(-1609931199)
    DataAccess.asLong(241593) should equal(939475976)
    DataAccess.asLong(53395) should equal(24677377)
    DataAccess.asLong(132759) should equal(-2002347261)
    DataAccess.asLong(116340) should equal(9964254)
    DataAccess.asLong(194556) should equal(140771403)
  }

  test("smartDiv") {
    DataAccess.smartDiv(0) should equal(1)
    DataAccess.smartDiv(1) should equal(1290)
    DataAccess.smartDiv(1972) should equal(961)
    DataAccess.smartDiv(1205) should equal(134)
    DataAccess.smartDiv(8414) should equal(961)
    DataAccess.smartDiv(1130) should equal(1)
    DataAccess.smartDiv(12511) should equal(1)
    DataAccess.smartDiv(7189) should equal(176)
    DataAccess.smartDiv(13039) should equal(1)
    DataAccess.smartDiv(4635) should equal(961)
    DataAccess.smartDiv(9203) should equal(690)
    DataAccess.smartDiv(10722) should equal(1)
    DataAccess.smartDiv(13254) should equal(1922)
    DataAccess.smartDiv(12381) should equal(1)
    DataAccess.smartDiv(10089) should equal(690)
    DataAccess.smartDiv(2322) should equal(1359)
    DataAccess.smartDiv(6654) should equal(961)
    DataAccess.smartDiv(4372) should equal(961)
    DataAccess.smartDiv(6333) should equal(1)
    DataAccess.smartDiv(12115) should equal(1359)
    DataAccess.smartDiv(442) should equal(1)
    DataAccess.smartDiv(7556) should equal(176)
    DataAccess.smartDiv(687) should equal(961)
    DataAccess.smartDiv(13536) should equal(3844)
    DataAccess.smartDiv(4620) should equal(690)
    DataAccess.smartDiv(4282) should equal(1)
    DataAccess.smartDiv(1820) should equal(1)
    DataAccess.smartDiv(8701) should equal(961)
    DataAccess.smartDiv(2642) should equal(3)
    DataAccess.smartDiv(4599) should equal(668)
    DataAccess.smartDiv(16150) should equal(107)
    DataAccess.smartDiv(8835) should equal(961)
    DataAccess.smartDiv(6274) should equal(168)
    DataAccess.smartDiv(2610) should equal(1)
    DataAccess.smartDiv(245) should equal(1)
    DataAccess.smartDiv(1368) should equal(961)
    DataAccess.smartDiv(1411) should equal(961)
    DataAccess.smartDiv(10216) should equal(1)
    DataAccess.smartDiv(11505) should equal(961)
    DataAccess.smartDiv(11053) should equal(113)
    DataAccess.smartDiv(8082) should equal(176)
  }
}
