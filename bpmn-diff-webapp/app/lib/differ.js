import _ from 'lodash';
import { create } from 'jsondiffpatch';
import ChangeHandler from './change-handler';


function Differ() { }


Differ.prototype.createDiff = function(a, b) {

  // create a configured instance, match objects by name
  var diffpatcher = create({
    objectHash: function(obj) {
      return obj.id || JSON.stringify(obj);
    }
  });

  return diffpatcher.diff(a, b);
};


Differ.prototype.diff = function(a, b, handler) {

  handler = handler || new ChangeHandler();

  function walk(diff, model) {

    _.forEach(diff, function(d, key) {

      // is array
      if (d._t === 'a') {

        _.forEach(d, function(val, idx) {

          if (idx === '_t') {
            return;
          }

          var removed = /^_/.test(idx),
              added = !removed && _.isArray(val),
              moved = removed && val[0] === '';

          idx = parseInt(removed ? idx.slice(1) : idx, 10);

          if (added || (removed && !moved)) {
            handler[removed ? 'removed' : 'added'](model, key, val[0], idx);
          } else
          if (moved) {
            handler.moved(model, key, val[1], val[2]);
          } else {
            walk(val, model[key][idx]);
          }
        });
      } else {
        if (_.isArray(d)) {
          handler.changed(model, key, d[0], d[1]);
        } else {
          handler.changed(model, key);
          walk(d, model[key]);
        }
      }
    });
  }

  var diff = this.createDiff(a, b);

  walk(diff, b, handler);

  return handler;
};

export default function diff(a, b, handler) {
  return new Differ().diff(a, b, handler);
};