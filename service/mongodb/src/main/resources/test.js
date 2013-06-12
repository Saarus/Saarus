var myFunction = function() {
  print('test store function') ;
}

db.system.js.save({'_id':'myFunction', 'value': myFunction });

db.eval('myFunction()') ;
