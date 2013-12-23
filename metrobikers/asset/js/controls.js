function setUpdateUrl(url)
{
    window.updateUrl = url;
}
function attachControl(el)
{
    var a = $(el);
    var ctrl;
    if (a.hasClass('datecontent'))
    {
        ctrl = new DateControl();
    }
    else if (a.hasClass('enumcontent'))
    {
        ctrl = new EnumControl();
    }
    else if (a.hasClass('imagecontent'))
    {
        ctrl = new ImageControl();
    }
    else
    {
        ctrl = new Control();
    }
    ctrl.setObj(a);
    ctrl.activateObj();


}


function Control()
{
    var thisObj = this;
    var obj;
    var inputControl;
    this.editField = function()
    {
        inputControl = thisObj.createInput();
        obj.hide();
        inputControl.insertBefore(obj);
        inputControl.focus();
    };
    this.createInput = function()
    {
        var inputControl = $("<input type='text' class='autoedit'></input>");
        inputControl.val(obj.text());
        inputControl.blur(thisObj.save);
        return inputControl;
    };
    this.setInputControl = function(input)
    {
        inputControl = input;
    };
    this.getInputControl = function()
    {
        return inputControl;
    };
    this.getObj = function() {
        return obj;
    };
    this.setObj = function(val) {
        obj = val;
    };
    this.activateObj = function()
    {
        obj.click(thisObj.editField)
                .attr("title", "Clicca per modificare")
                .focus(thisObj.editField);
    };
    this.getInputValue = function()
    {
        return inputControl.val();
    };

    this.unformatData = function()
    {
        return inputControl.val();
    };
    this.onAfterSetValue = function() {
    };
    this.save = function(event)
    {
        if (event.relatedTarget && $(event.relatedTarget).closest(this).length > 0)
            return;
        var value = thisObj.getInputValue();
        var oldValue = obj.text();
        var modified = oldValue !== value;
        inputControl.remove();
        obj.show();
        if (modified)
        {
            obj.text(value);
            thisObj.onAfterSetValue();
            $.post(window.updateUrl, thisObj.getPostData(), function(res) {
                if (!res || !res.result)
                {
                    obj.text(oldValue);
                    thisObj.onAfterSetValue();
                }
            }, 'json');
        }
    };
    this.undo = function()
    {
        inputControl.remove();
        obj.show();
    };
    this.getPostData = function()
    {
        var data = {};
        data[obj.attr("name")] = thisObj.unformatData();
        return data;
    };
}

function DateControl()
{
    Control.call(this);
    var thisObj = this;

    this.createInput = function()
    {
        var inputControl = $("<input type='text' class='autoedit'></input>");
        inputControl.datepicker({"autoSize": true, "dateFormat": "dd/mm/yy", "onSelect": thisObj.save, "onClose": thisObj.undo});
        inputControl.val(thisObj.getObj().text());
        return inputControl;
    };

    this.unformatData = function()
    {
        var currentDate = $.datepicker.parseDate('dd/mm/yy', thisObj.getInputControl().val());
        return currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
    };
}
DateControl.prototype = new Control();

function EnumControl()
{
    Control.call(this);
    var thisObj = this;
    var items = null;
    function getItems()
    {
        if (!items)
            items = eval(thisObj.getObj().attr('items'));
        return items;
    }
    this.createInput = function()
    {
        var html = "<div style='display:inline-block;' tabindex=1>";
        for (var i = 0; i < getItems().length; i++)
        {
            var item = getItems()[i];
            var selected = item === thisObj.getObj().text();
            html += '<input type="radio" value="' + i + '"' + (selected ? ' checked' : '') + ' name="' + thisObj.getObj().attr('name') + '"/>' + item + '<br>';
        }
        html += "</div>";
        var input = $(html);
        //input.blur(thisObj.save);
        $('input', input).change(thisObj.save);
        return input;
    };
    this.getInputValue = function()
    {
        var index = $('input::checked', thisObj.getInputControl()).val();
        return getItems()[index];
    };
    this.unformatData = function()
    {
        return $('input::checked', thisObj.getInputControl()).val();
    };
}
EnumControl.prototype = new Control();

function ImageControl()
{
    Control.call(this);
    var thisObj = this;

    this.createInput = function()
    {
        var html = '<form style="display:none;" method="post" action="/user/upload_photo" enctype="multipart/form-data"><input type="file" name="userfile" value="Cambia..."/></form>';
        var input = $(html);
        $('input', input).change(function(){$(this).closest('form').submit();}).trigger('click');
        return input;
    };

    this.save = function()
    {
        thisObj.getInputControl().submit();

    };
    this.activateObj = function()
    {
        thisObj.getObj().click(thisObj.editField)
                .attr("title", "Clicca per modificare");
    };
    this.editField = function()
    {
        var c = thisObj.createInput();
        c.insertBefore(thisObj.getObj());
        thisObj.setInputControl(c);
    };
}
ImageControl.prototype = new Control();