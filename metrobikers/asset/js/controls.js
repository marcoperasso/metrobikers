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
    else if (a.hasClass('gendercontent'))
    {
        ctrl = new GenderControl();
    }
    else if (a.hasClass('postcontent'))
    {
        ctrl = new PostControl();
    }
    else
    {
        ctrl = new Control();
    }
    ctrl.setObj(a);
    a.click(ctrl.editField)
            .attr("title", "Clicca per modificare")
            .focus(ctrl.editField);

}


function Control()
{
    var thisObj = this;
    var obj;
    var inputControl;
    this.setThisObj = function(val)
    {
        thisObj = val;
    };
    this.editField = function()
    {
        inputControl = thisObj.createInput();
        obj.hide();
        inputControl.insertBefore(obj);
        inputControl.focus();
    };
    this.createInput = function()
    {
        var inputControl = $("<input type='text'></input>");
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
    this.getInputValue = function()
    {
        return inputControl.val();
    };

    this.unformatData = function()
    {
        return inputControl.val();
    };
    this.save = function()
    {
        var value = thisObj.getInputValue();
        var modified = obj.text() !== value;
        //obj.click(editField).focus(editField);
        inputControl.remove();
        obj.show();
        if (modified)
        {
            $.post(window.updateUrl, thisObj.getPostData(), function(res) {
                if (res && res.result)
                    obj.text(value);
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
    var thisObj = this;
    this.setThisObj(this);

    this.createInput = function()
    {
        var inputControl = $("<input type='text'></input>");
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

function GenderControl()
{
    var thisObj = this;
    this.setThisObj(this);
    var items = ['Non specificato', 'Femmina', 'Maschio'];
    this.createInput = function()
    {
        var html = "<div style='display:inline;' tabindex=1>";
        for (var i = 0; i < items.length; i++)
        {
            var item = items[i];
            var selected = item === thisObj.getObj().text();
            html += '<input type="radio" value="' + i + '"' + (selected ? ' checked' : '') + ' name="' + thisObj.getObj().attr('name') + '"/>' + item;
        }
        html += "</div>";
        var input = $(html);
        $('input', input).change(thisObj.save);
        return input;
    };
    this.getInputValue = function()
    {
        var index = $('input::checked', thisObj.getInputControl()).val();
        return items[index];
    };
    this.unformatData = function()
    {
        return $('input::checked', thisObj.getInputControl()).val();
    };
}
GenderControl.prototype = new Control();

function PostControl()
{
    var thisObj = this;
    this.setThisObj(this);

    this.getPostData = function()
    {
        var data = {};
        data.postcontent = thisObj.unformatData();
        data.posttime = 0;
        return data;
    };

}
PostControl.prototype = new Control();