function Shapes() {
  this.shapes = {} ;

  this.put = function(key, obj) { this.shapes[key] = obj ; };

  this.get = function(key) { return this.shapes[key] ; };

  this.moveByDelta = function(dx, dy) {
  }
};

function Entity(name, desc, posX, posY) {
  this.name = name ;
  this.description = desc;
  this.posX = posX ? posX : 15
  this.posY = posY ? posY : 15
  this.shape = null ;
  this.connectTo = null ;
  this.connectFrom = null ;
  
  var thisEntity = this ;

  var onDrag = function (x, y) {
    this.animate({"fill-opacity": .2}, 500);
  };

  var getConnectPathCoord = function (fromEntity, toEntity) {
    var box1 = fromEntity.shape.box.getBBox() ; 
    var box2 = toEntity.shape.box.getBBox() ; 
    var from = {
      "x": box1.x + box1.width,
      "y": box1.y + box1.height/2,
    } ;
    var to = {
      "x": box2.x,
      "y": box2.y + box2.height/2,
    } ;

    var pathCoord = [
      ['M', from.x, from.y], ['L', to.x, to.y]
    ];
    return pathCoord ;
  };

  var onMove = function(dx, dy) {
    var transform  = ["t", dx,  dy] ;
    thisEntity.shape.input.transform(transform) ;
    thisEntity.shape.output.transform(transform) ;
    thisEntity.shape.box.transform(transform) ;

    if(thisEntity.connectTo != null) {
      var pathCoord = getConnectPathCoord(thisEntity, thisEntity.connectTo.entity); 
      thisEntity.connectTo.path.attr({"path": pathCoord}) ;
    }
    if(thisEntity.connectFrom != null) {
      var pathCoord = getConnectPathCoord(thisEntity.connectFrom.entity, thisEntity); 
      thisEntity.connectFrom.path.attr({"path": pathCoord}) ;
    }
  };

  var onDrop = function () {
    this.animate({"fill-opacity": 0}, 500);
  };

  this.connect = function(other) {
    this.connectTo = { "entity": other };
    other.connectFrom = { "entity": this };
  };

  this.draw = function(canvas) {
    var attr = { 
      "fill": "red", "stroke": "red", "fill-opacity": 0, "stroke-width": 2, "cursor": "move"
    };
    var box = canvas.rect(this.posX, this.posY, 120, 60, 5);
    box.attr(attr);
    var input = canvas.circle(this.posX, this.posY + 30, 5);
    input.attr(attr);

    var output = canvas.circle(this.posX + 120, this.posY + 30, 5);
    output.attr(attr);

    box.drag(onMove, onDrag, onDrop);
    this.shape = { "box": box, "input": input, "output": output} ;
  };

  this.drawConnection = function(canvas) {
    if(this.connectTo == null) return ;
    var pathCoord = getConnectPathCoord(this, this.connectTo.entity); 
    var path = canvas.path(pathCoord) ;
    path.attr({"stroke": "white", "stroke-width": 2, "fill": "none"}) ;
    this.connectTo.path = path ;
    this.connectTo.entity.connectFrom.path = path ;
  };
}

function Diagram() {
  this.canvas = Raphael("Canvas", 800, 480);
  this.entities = [] ;

  this.addEntity = function(entity) {
    this.entities.push(entity) ;
  };
  
  this.draw = function() {
    for(var i = 0; i < this.entities.length; i++) {
      this.entities[i].draw(this.canvas) ;
    }
    for(var i = 0; i < this.entities.length; i++) {
      this.entities[i].drawConnection(this.canvas) ;
    }
  }
};

window.onload = function () {
  var diagram = new Diagram() ;

  var entity1 =  new Entity('test 1', 'test entity 1', 20, 20) ;
  var entity2 =  new Entity('test 2', 'test entity 2', 200, 100) ;
  var entity3 =  new Entity('test 3', 'test entity 3', 400, 100) ;

  entity1.connect(entity2) ; 
  entity2.connect(entity3) ; 
  
  diagram.addEntity(entity1) ;
  diagram.addEntity(entity2) ;
  diagram.addEntity(entity3) ;

  diagram.draw() ;
};
