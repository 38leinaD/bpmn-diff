var BpmnJS = require('bpmn-js/lib/Viewer');

BpmnJS.prototype._modules = BpmnJS.prototype._modules.concat([
  require('bpmn-js/lib/features/movecanvas'),
  require('bpmn-js/lib/features/zoomscroll')
]);

module.exports = BpmnJS;