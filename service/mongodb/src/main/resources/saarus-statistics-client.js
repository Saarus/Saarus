function statistics(collName) {
  var cursor = db[collName].find() ;
  var stats = { }  ;
  var count = 0 ;
  while(cursor.hasNext()) {
    var record = cursor.next() ;
    for(key in record) {
      if(key.startsWith('_')) continue ;
      var val = record[key]; 

      if(val != null && val != 0) {
        if(stats[key] == null) {
          stats[key] = oy.UVar() ;
        }
        stats[key].inc(1) ;
      }
    }
    count++ ;
    if(count % 1000 == 0) {
      print('Process ' + count + ' records!!!!!!!!!!') ;
    }
  }

  var statRecord = { '_id': collName } ;
  for(key in stats) {
    var fieldStat = stats[key].calc() ;
    statRecord[key] = {
      'count'              :   fieldStat.count,
      'sum'                :   fieldStat.sum,
      'min'                :   fieldStat.min,
      'max'                :   fieldStat.max,
      'average'            :   fieldStat.average,
      'variance'           :   fieldStat.variance,
      'standardDeviation'  :   fieldStat.standardDeviation,
      'skew'               :   fieldStat.skew,
      'kurtosis'           :   fieldStat.kurtosis,
      'bad'                :   fieldStat.bad
    } ;
  }
  print(JSON.stringify(statRecord, undefined, 2)) ;
  var statColl = db.getCollection('saarus_statistics') ;
  statColl.save(statRecord) ;
}

statistics('user') ;
