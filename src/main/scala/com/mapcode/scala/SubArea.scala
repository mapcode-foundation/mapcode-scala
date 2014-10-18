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

import org.slf4j.LoggerFactory

case class SubArea(latRange: Range,
                   lonRange: Range,
                   boundedLatRange: Seq[Range],
                   boundedLonRange: Seq[Range],
                   parentTerritory: Territory.Territory,
                   subAreaID: Integer)

object SubArea {

}

/*
object SubArea {

  val LOG = LoggerFactory.getLogger(classOf[SubArea])

  private static final ArrayList<SubArea>                   subAreas = new ArrayList<SubArea>();
  private static final TreeMap<Integer, ArrayList<SubArea>> lonMap   = new TreeMap<Integer, ArrayList<SubArea>>();
  private static final TreeMap<Integer, ArrayList<SubArea>> latMap   = new TreeMap<Integer, ArrayList<SubArea>>();

  private static final Range<Integer> latBoundingRange =
    new Range<Integer>(Point.LAT_MICRODEG_MIN, Point.LAT_MICRODEG_MAX);
  private static final Range<Integer> lonBoundingRange =
    new Range<Integer>(Point.LON_MICRODEG_MIN, Point.LON_MICRODEG_MAX);

  static {
    for (final Territory territory : Territory.values()) {
      final int territoryCode = territory.getTerritoryCode();
      final int first = DataAccess.dataFirstRecord(territoryCode);
      final int last = DataAccess.dataLastRecord(territoryCode);

      // Add a number sub areas.
      for (int i = subAreas.size(); i <= last; i++) {
        subAreas.add(null);
      }
      for (int i = last; i >= first; i--) {
        final SubArea newSubArea = new SubArea(i, territory, subAreas.get(last));
        subAreas.set(i, newSubArea);

        if ((newSubArea.boundedLatRange == null) || (newSubArea.boundedLonRange == null)) {
          continue;
        }

        for (final Range<Integer> longitudeRange : newSubArea.boundedLonRange) {
          if (!lonMap.containsKey(longitudeRange.getMin())) {
            lonMap.put(longitudeRange.getMin(), new ArrayList<SubArea>());
          }
          if (!lonMap.containsKey(longitudeRange.getMax())) {
            lonMap.put(longitudeRange.getMax(), new ArrayList<SubArea>());
          }
        }

        for (final Range<Integer> latitudeRange : newSubArea.boundedLatRange) {
          if (!latMap.containsKey(latitudeRange.getMin())) {
            latMap.put(latitudeRange.getMin(), new ArrayList<SubArea>());
          }
          if (!latMap.containsKey(latitudeRange.getMax())) {
            latMap.put(latitudeRange.getMax(), new ArrayList<SubArea>());
          }
        }
      }
    }
    for (final SubArea subArea : subAreas) {
      if ((subArea.boundedLatRange == null) || (subArea.boundedLonRange == null)) {
        continue;
      }
      SortedMap<Integer, ArrayList<SubArea>> subMap;

      for (final Range<Integer> longitudeRange : subArea.boundedLonRange) {
        subMap = lonMap.subMap(longitudeRange.getMin(), longitudeRange.getMax() + 1);
        for (final ArrayList<SubArea> areaList : subMap.values()) {
          areaList.add(subArea);
        }
      }

      for (final Range<Integer> latitudeRange : subArea.boundedLatRange) {
        subMap = latMap.subMap(latitudeRange.getMin(), latitudeRange.getMax() + 1);
        for (final ArrayList<SubArea> areaList : subMap.values()) {
          areaList.add(subArea);
        }
      }

    }
    LOG.debug("SubArea (init): lat=[{}, {}], lon=[{}, {}]",
      Point.microDegToDeg(latMap.firstKey()), Point.microDegToDeg(latMap.lastKey()),
      Point.microDegToDeg(lonMap.firstKey()), Point.microDegToDeg(lonMap.lastKey()));
  }
}
*/
