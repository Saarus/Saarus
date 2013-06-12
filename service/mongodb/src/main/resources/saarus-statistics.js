function statistics(collName) {
  var map = function() {
    for(key in this) {
      if(key.startsWith('_')) continue ;
      var val = this[key]; 
      if(val != null && val != 0) {
        emit(key, { count: 1 });
      }
    }
    emit('_recordCount', { count: 1});
  };

  var reduce = function(key , values ) {
    //var org = db.system.js.findOne({'_id':'org'}).value;
    //var oy = db.system.js.findOne({'_id':'oy'}).value;
    var field = { 'count': 0 };
    for ( var i = 0; i < values.length; i ++ ) {
      field.count += values[i].count ;
    };
    return field;
  };

  var options = {
    finalize: function(k,v) { 
      var us = oy.UVar() ;
      us.inc(1) ;
      return v;
    },
    //scope: {'org': org, 'oy': oy } ,
    //out:  'table_name'
    out:  { inline: 1 }
    //query: ord_date: { $gt: new Date('01/01/2012')},
  }
  
  var output = db.user.mapReduce(map, reduce, options) ;
  var record = {'_id': collName } ;
  for(var i = 0; i < output.results.length; i++) {
    var field = output.results[i]._id ;
    var result = output.results[i].value ;
    //var us = oy.UVar() ;
    //us.inc(result.count) ;
    record[field] = result ;
  }
  var statColl = db.getCollection('saarus_statistics') ;
  statColl.save(record) ;
  print(JSON.stringify(record)) ;
}

statistics('user') ;
