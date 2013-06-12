function extract(collName, idField, mapFields, outCollection) {

  var map = function() {
    var values = {} ;
    for(var i = 0; i < mapFields.length; i++) {
      var mapField = mapFields[i];
      values[mapField.mapField] = this[mapField.field] ;
    }
    emit(this[idField], values);
  };

  var reduce = function(key , values ) {
    var result = {} ;
    for ( var i = 0; i < values.length; i ++ ) {
      for(key in values[i]) {
        result[key] = values[i][key] ;
      }
    };

    return result ;
  };
  
  var options = {
    'out': outCollection , 
    'scope': {
      'idField': idField,
      'mapFields': mapFields
    }
  };
  db.user.mapReduce(map, reduce, options) ;
}

var reviewMapFields = [
  {'field': 'review_count', 'mapField': 'user_review_count'},
  {'field': 'average_stars', 'mapField': 'user_average_stars'},
]
extract('user', 'user_id', userMapFields, 'mldata') ;

var userMapFields = [
  {'field': 'review_count', 'mapField': 'user_review_count'},
  {'field': 'average_stars', 'mapField': 'user_average_stars'},
]
extract('user', 'user_id', userMapFields, 'mldata') ;
