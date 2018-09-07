app.directive('stringToNumber', function() {
  return {
    require: 'ngModel',
    link: function(scope, element, attrs, ngModel) {
      ngModel.$parsers.push(function(value) {
        return '' + value;
      });
      ngModel.$formatters.push(function(value) {
        return parseFloat(value);
      });
    }
  };
});

app.directive('dateformat', function() {
  return {
    require: 'ngModel',
    link: function(scope, element, attrs, ngModel) {
      ngModel.$parsers.push(function(value) {
          var d = new Date(value)
        return d;
      });
      ngModel.$formatters.push(function(value) {
          var d = new Date(value)
        return d;
      });
    }
  };
});

app.directive('barprogress', function(){
    function link(scope, element, attrs){
        scope.$watch('data', function(value){
            scope.percentage = value.used/value.total * 100;
        });
    }
    return{
        restrict: 'EA',
        replace: true,
        scope: {
            data:'='
                },
        link: link,
        template:'<div class="bar-progress"><div class="icon"><i class="fa fa-wifi"></i></div><div class="back"><div class="front" style="width:{{percentage}}%"></div></div><div class="value tc">{{data.used}}/{{data.total}}</div></div>'      
    };
});
app.directive('fileUpload', function () {
	   return {
	       scope: true,        //create a new scope
	       link: function (scope, el, attrs) {
	           el.bind('change', function (event) {
	               var files = event.target.files;
	               //iterate files since 'multiple' may be specified on the element
	               for (var i = 0;i<files.length;i++) {
	                   //emit event upward
	                   scope.$emit("fileSelected", { file: files[i] });
	               }                                       
	           });
	       }
	   };
	});

