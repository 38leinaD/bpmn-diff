import './tree-view.js'

//import BpmnViewer from 'https://unpkg.com/bpmn-js@3.4.3/lib/Viewer.js?module'
import BpmnViewer from 'bpmn-js/lib/NavigatedViewer.js'
import diff from './lib/differ.js'

const BACKEND_URI = (location.host === 'localhost:3000' && window.cy === undefined) ? 'http://localhost:8080' : '..';
const versionDiv = {
  left: document.querySelector("#versionLeft"),
  right: document.querySelector("#versionRight")
};
let diffCommand = true;

window.addEventListener("unload", onBrowserClosed, false);

function onBrowserClosed() {
  navigator.sendBeacon("../exit", {});
}

function createViewer(side) {
  return new BpmnViewer({
    container: '#canvas-' + side,
    height: '100%',
    width: '100%'
  });
}

var changing;

function syncNowFn(movedViewer, viewer) {

  return function (e) {
    if (changing) {
      return;
    }

    changing = true;
    viewer.get('canvas').viewbox(movedViewer.get('canvas').viewbox());
    changing = false;
  };
}

function syncViewers(a, b) {



  function syncViewbox(a, b) {
    a.on('canvas.viewbox.changing', syncNowFn(a, b));
  }

  syncViewbox(a, b);
  syncViewbox(b, a);
}

function createViewers(left, right) {

  var sides = {};

  sides[left] = createViewer(left);
  sides[right] = createViewer(right);

  // sync navigation
  syncViewers(sides[left], sides[right]);

  return sides;
}


var viewers = createViewers('left', 'right');

function getViewer(side) {
  return viewers[side];
}

function getOtherViewer(viewer) {
  return getViewer('left') == viewer ? getViewer('right') : getViewer('left');
}

function isLoaded(v) {
  return v.loading !== undefined && !v.loading;
}

function allDiagramsLoaded() {
  return isLoaded(viewers['left']) && isLoaded(viewers['right']);
}

function setLoading(viewer, loading) {
  viewer.loading = loading;
}


function clearDiffs(viewer) {
  viewer.get('overlays').remove({ type: 'diff' });

  viewer.get('elementRegistry').getAll().forEach(function (container) {
    var gfx = viewer.get('elementRegistry').getGraphics(container.id);

    gfx.classList.remove('diff-added');
    gfx.classList.remove('diff-changed');
    gfx.classList.remove('diff-removed');
    gfx.classList.remove('diff-layout-changed');
  });

}


function diagramLoading(side, viewer) {

  setLoading(viewer, true);
  var loaded = [];
  if (isLoaded(viewers['left'])) loaded.push(viewers['left']);
  if (isLoaded(viewers['right'])) loaded.push(viewers['right']);

  // clear diffs on loaded
  loaded.forEach(function (v) {
    clearDiffs(v);
  });
}

function diagramLoaded(err, side, viewer) {
  if (err) {
    console.error('load error', err);
    alert("Error loading BPMN-flow. " + err)
  }

  setLoading(viewer, err ? err : false);
  if (allDiagramsLoaded() && diffCommand) {

    // sync viewboxes
    var other = getViewer(side == 'left' ? 'right' : 'left');
    viewer.get('canvas').viewbox(other.get('canvas').viewbox());

    showDiff(getViewer('left'), getViewer('right'));
  }
}

function loadDiagram(side, diagram) {

  var viewer = getViewer(side);

  function done(err) {
    diagramLoaded(err, side, viewer);
  }

  diagramLoading(side, viewer);
  versionDiv[side].innerText = diagram.file;

  if (diagram.xml) {
    return viewer.importXML(diagram.xml, done);
  }

  fetch(diagram.url)
    .then(response => response.text())
    .then(xml => viewer.importXML(xml, done));
}

function each(obj, fn) {
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      const elem = obj[key];
      fn(key, elem);
    }
  }
}

function toggle(id) {
  const elem = document.getElementById(id);
  if (!elem) return;
  elem.style.display == "block" ? "none" : "block";
}

function showDiff(viewerOld, viewerNew) {

  var result = diff(viewerOld._definitions, viewerNew._definitions);

  each(result._removed, function (i, obj) {
    highlight(viewerOld, i, 'diff-removed');
    addMarker(viewerOld, i, 'marker-removed', '&minus;');
  });


  each(result._added, function (i, obj) {
    highlight(viewerNew, i, 'diff-added');
    addMarker(viewerNew, i, 'marker-added', '&#43;');
  });


  each(result._layoutChanged, function (i, obj) {
    highlight(viewerOld, i, 'diff-layout-changed');
    addMarker(viewerOld, i, 'marker-layout-changed', '&#8680;');

    highlight(viewerNew, i, 'diff-layout-changed');
    addMarker(viewerNew, i, 'marker-layout-changed', '&#8680;');
  });


  each(result._changed, function (i, obj) {

    highlight(viewerOld, i, 'diff-changed');
    addMarker(viewerOld, i, 'marker-changed', '&#9998;');

    highlight(viewerNew, i, 'diff-changed');
    addMarker(viewerNew, i, 'marker-changed', '&#9998;');

    var details = '<table ><tr><th>Attribute</th><th>old</th><th>new</th></tr>';
    each(obj.attrs, function (attr, changes) {
      details = details + '<tr>' +
        '<td>' + attr + '</td><td>' + changes.oldValue + '</td>' +
        '<td>' + changes.newValue + '</td>' +
        '</tr>';
    });

    details = details + '</table></div>';

    viewerOld.get('elementRegistry').getGraphics(i).onclick = function (event) {
      toggle('changeDetailsOld_' + i);
    };

    var detailsOld = '<div id="changeDetailsOld_' + i + '" class="changeDetails">' + details;

    // attach an overlay to a node
    viewerOld.get('overlays').add(i, 'diff', {
      position: {
        bottom: -5,
        left: 0
      },
      html: detailsOld
    });

    toggle('#changeDetailsOld_' + i);

    viewerNew.get('elementRegistry').getGraphics(i).onclick = function (event) {
      toggle('#changeDetailsNew_' + i);
    };

    var detailsNew = '<div id="changeDetailsNew_' + i + '" class="changeDetails">' + details;

    // attach an overlay to a node
    viewerNew.get('overlays').add(i, 'diff', {
      position: {
        bottom: -5,
        left: 0
      },
      html: detailsNew
    });

    toggle('#changeDetailsNew_' + i);
  });

  // create Table Overview of Changes
  showChangesOverview(result, viewerOld, viewerNew);

}

function openDiagram(xml, side, file) {
  loadDiagram(side, { xml: xml, file: file.name });
}

function openFile(file, target, done) {
  var reader = new FileReader();

  reader.onload = function (e) {
    var xml = e.target.result;
    done(xml, target, file);
  };

  reader.readAsText(file);
}

// only temporary
function createFromTemplate(html) {
  var template = document.createElement('template');
  html = html.trim();
  template.innerHTML = html;
  return template.content.firstChild;
}


[...document.querySelectorAll('.drop-zone')].forEach(elem => {
  elem.appendChild(createFromTemplate('<div class="drop-marker" />'));

  function removeMarker() {
    [...document.querySelectorAll('.drop-zone')].forEach(e => e.removeClass('dropping'));
  }

  function handleFileSelect(e) {
    e.stopPropagation();
    e.preventDefault();

    var files = e.dataTransfer.files;
    openFile(files[0], elem.getAttribute('target'), openDiagram);

    removeMarker();
  }

  function handleDragOver(e) {
    removeMarker();

    e.stopPropagation();
    e.preventDefault();

    element.addClass('dropping');

    e.dataTransfer.dropEffect = 'copy';
  }

  function handleDragLeave(e) {
    removeMarker();
  }

  elem.addEventListener('dragover', handleDragOver, false);
  elem.ownerDocument.body.addEventListener('dragover', handleDragLeave, false);

  elem.addEventListener('drop', handleFileSelect, false);
});

[...document.querySelectorAll('.file')].forEach(
  elem => elem.addEventListener('change', event =>
    openFile(event.target.files[0], elem.getAttribute('target'), openDiagram)));

function addMarker(viewer, elementId, className, symbol) {

  var overlays = viewer.get('overlays');

  try {
    // attach an overlay to a node
    overlays.add(elementId, 'diff', {
      position: {
        top: -12,
        right: 12
      },
      html: '<span class="marker ' + className + '">' + symbol + '</span>'
    });
  } catch (e) {
    // fuck you, haha
  }
}

function highlight(viewer, elementId, marker) {
  viewer.get('canvas').addMarker(elementId, marker);
}

function unhighlight(viewer, elementId, marker) {
  viewer.get('canvas').removeMarker(elementId, marker);
}

document.querySelector('#changes-overview .show-hide-toggle').onclick = function () {
  document.querySelector('#changes-overview').classList.toggle('collapsed');
};

function clearChangesOverview() {
  const table = document.querySelector('#changes-overview table');
  if (table != null) table.remove();
}

function showChangesOverview(result, viewerOld, viewerNew) {

  clearChangesOverview();

  var changesTable = createFromTemplate(
    '<table>' +
    '<thead><tr><th>#</th><th>Name</th><th>Type</th><th>Change</th></tr></thead>' +
    '</table>');

  var count = 0;

  function addRow(element, type, label) {
    var html =
      '<tr class="entry">' +
      '<td>' + (count++) + '</td><td>' + (element.name || '') + '</td>' +
      '<td>' + element.$type.replace('bpmn:', '') + '</td>' +
      '<td><span class="status">' + label + '</span></td>' +
      '</tr>';

    var row = createFromTemplate(html);
    row.dataset.changed = type;
    row.dataset.element = element.id;
    row.classList.add(type);

    changesTable.appendChild(row);
  }

  each(result._removed, function (i, obj) {
    addRow(obj, 'removed', 'Removed');
  });

  each(result._added, function (i, obj) {
    addRow(obj, 'added', 'Added');
  });

  each(result._changed, function (i, obj) {
    addRow(obj.model, 'changed', 'Changed');
  });

  each(result._layoutChanged, function (i, obj) {
    addRow(obj, 'layout-changed', 'Layout Changed');
  });

  document.querySelector('#changes-overview .changes').appendChild(changesTable);

  var HIGHLIGHT_CLS = 'highlight';

  [...document.querySelectorAll('#changes-overview tr.entry')].forEach(elem => {

    var id = elem.dataset.element;
    var changed = elem.dataset.changed;

    elem.onmouseover = _ => {

      if (changed == 'removed') {
        highlight(viewerOld, id, HIGHLIGHT_CLS);
      } else if (changed == 'added') {
        highlight(viewerNew, id, HIGHLIGHT_CLS);
      } else {
        highlight(viewerOld, id, HIGHLIGHT_CLS);
        highlight(viewerNew, id, HIGHLIGHT_CLS);
      }
    }

    elem.onmouseout = _ => {

      if (changed == 'removed') {
        unhighlight(viewerOld, id, HIGHLIGHT_CLS);
      } else if (changed == 'added') {
        unhighlight(viewerNew, id, HIGHLIGHT_CLS);
      } else {
        unhighlight(viewerOld, id, HIGHLIGHT_CLS);
        unhighlight(viewerNew, id, HIGHLIGHT_CLS);
      }
    };

    elem.onclick = _ => {

      var containerWidth = document.querySelector('.di-container').clientWidth;

      var containerHeight = document.querySelector('.di-container').clientHeight;

      var viewer = (changed == 'removed' ? viewerOld : viewerNew);

      var element = viewer.get('elementRegistry').get(id);

      var x, y;

      if (element.waypoints) {
        x = element.waypoints[0].x;
        y = element.waypoints[0].y;
      } else {
        x = element.x + element.width / 2;
        y = element.y + element.height / 2;
      }

      viewer.get('canvas').viewbox({
        x: x - (containerWidth / 2),
        y: y - ((containerHeight / 2) - 100),
        width: containerWidth,
        height: containerHeight
      });
      syncNowFn(viewer, getOtherViewer(viewer))();
    };

  });
}

function bfsSearchFile(files) {
  for (let file of files) {
    if (file.children === undefined && file.supported) {
      return file;
    }
  }
  for (let file of files) {
    if (file.children !== undefined) {
      const foundFile = bfsSearchFile(file.children);
      if (foundFile != null) return foundFile;
    }
  }
}

function recursiveWalk(node, callback) {
  callback(node);
  if ('children' in node) {
    node.children.forEach(node => recursiveWalk(node, callback));
  }
}

function diffToTree(diff) {
  const cb = (node) => {
    if ('type' in node) {
      node.diffType = node.type; // TODO: Rename what comes from server
      node.type = 'file'
      node.hint = node.diffType;
    }
    else {
      node.type = 'folder';
    }

    if ('leftName' in node) {
      node.label = node.leftName;
    }
    else if ('rightName' in node) {
      node.label = node.rightName;
    }
  };

  diff.forEach(node => recursiveWalk(node, cb));

  return diff;
}

function main() {
  fetch(`${BACKEND_URI}/diff`)
    .then(r => r.json())
    .then(diff => {
      if (Array.isArray(diff)) {
        // folder diff
        customElements.whenDefined("tree-view")
          .then(_ => {
            const fileBrowser = document.querySelector("tree-view");

            const tree = diffToTree(diff);
            fileBrowser.data = tree;

            fileBrowser.expandLeafsByCondition((node) => true)

            fileBrowser.addEventListener("node-selected", e => {
              const diff = e.detail;

              if (!diff.supported) {
                getViewer('left').clear();
                getViewer('right').clear();
                alert("Is this a BPMN file? Can only diff BPMN files. ")
                return;
              }

              clearChangesOverview();

              versionDiv['left'].innerText = '-';
              versionDiv['right'].innerText = '-';

              if (diff.diffType == "Modified") {
                diffCommand = true;
              }
              else {
                diffCommand = false;
              }
              if (diff.diffType == "Removed" || diff.diffType == "Modified") {
                loadDiagram('left', { url: `${BACKEND_URI}/diff/${diff.id}/left`, file: diff.leftName });
              }
              if (diff.diffType == "Added" || diff.diffType == "Modified") {
                loadDiagram('right', { url: `${BACKEND_URI}/diff/${diff.id}/right`, file: diff.rightName });
              }

              if (diff.diffType == "Removed") {
                getViewer('right').clear();
              }
              else if (diff.diffType == "Added") {
                getViewer('left').clear();
              }
            });

            /*fileBrowser.ls(diff);
            */
            const foundFile = bfsSearchFile(diff);
            if (foundFile) {
              fileBrowser.select(foundFile);
            }

          });
      }
      else {
        // file diff
        loadDiagram('left', { url: `${BACKEND_URI}/diff/${diff.id}/left`, file: diff.leftName });
        loadDiagram('right', { url: `${BACKEND_URI}/diff/${diff.id}/right`, file: diff.rightName });

        document.querySelector('main').classList.add('collapsedNav');
      }
    });

}

main();
