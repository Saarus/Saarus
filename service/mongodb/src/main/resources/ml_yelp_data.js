var getObjectProperty = function(o, s) {
  s = s.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
  s = s.replace(/^\./, '');           // strip a leading dot
  var a = s.split('.');
  while (a.length) {
    var n = a.shift();
    if (n in o) {
      o = o[n];
    } else {
      return;
    }
  }
  return o;
}

/*
var test = {
  'test1': 'test1',
  'test2': { 'test': 'test'},
  'array': [{'test': 'test0', 'test': 'test1'}]
};

print('test1 = ' +  getObjectProperty(test, 'test1')) ;
print('test2.test = ' +  getObjectProperty(test, 'test2.test')) ;
print('array[0].test1 = ' +  getObjectProperty(test, 'array[0].test')) ;
print('array[1].test2 = ' +  getObjectProperty(test, 'array[1].test')) ;
*/
db.system.js.save(
  {
    _id : "getObjectProperty" ,
     value : getObjectProperty
  }
);


function extract(collName, mapFields, outCollection) {
  var cursor = db[collName].find() ;
  var holder = [] ;
  var count = 0 ;
  while(cursor.hasNext()) {
    var record = cursor.next() ;
    var values = {} ;
    for(var i = 0; i < mapFields.length; i++) {
      var mapField = mapFields[i];
      values[mapField.toField] = getObjectProperty(record, mapField.field);
    }
    holder[holder.length] = values ;
    count++ ;
    if(count % 1000 == 0) {
      db[outCollection].insert(holder) ;
      holder = [] ;
      print('Process ' + count + ' records!!!!!!!!!!') ;
    }
  }
  if(holder.length > 0) {
    db[outCollection].insert(holder) ;
    print('Process ' + count + ' records!!!!!!!!!!') ;
  }
}

function merge(collName, mapFields, matchField, outCollection) {
  var cursor = db[collName].find() ;
  var count = 0 ;
  while(cursor.hasNext()) {
    var record = cursor.next() ;
    var values = {} ;
    for(var i = 0; i < mapFields.length; i++) {
      var mapField = mapFields[i];
      values[mapField.toField] = getObjectProperty(record, mapField.field);
    }
    
    db[outCollection].update(
      { 'user_id' : record[matchField] },
      { $set: values },
      { multi: true }
    );
    count++ ;
    if(count % 1000 == 0) {
      print('Process ' + count + ' records!!!!!!!!!!') ;
    }
  }
  print('Process ' + count + ' records!!!!!!!!!!') ;
}

db['mldata'].drop() ;
var reviewMapFields = [
  {'field': 'review_id', 'toField': 'review_id'},
  {'field': 'user_id', 'toField': 'user_id'},
  {'field': 'businese_id', 'toField': 'businese_id'},
  {'field': 'stars', 'toField': 'stars'},
  {'field': 'votes.funny', 'toField': 'vote_funny'},
  {'field': 'votes.useful', 'toField': 'vote_useful'},
  {'field': 'votes.cool', 'toField': 'vote_cool'}
]
extract('review', reviewMapFields, 'mldata') ;

var userMapFields = [
  {'field': 'review_count', 'toField': 'user_review_count'},
  {'field': 'average_stars', 'toField': 'user_average_stars'},
]
merge('user', userMapFields, 'user_id', 'mldata') ;
