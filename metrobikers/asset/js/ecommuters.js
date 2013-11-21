/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$(function() {
    setContentHeight();
    $(window).resize(setContentHeight);
    $(".changeable").click(editField);
});
function setContentHeight()
{
    $("body").css("min-height", ($(window).height()) + "px");
}




function getInput(obj)
{
    var bag = new InputBag(obj);
    if (obj.hasClass('datecontent'))
    {
        var input = $("<input type='text'></input>");
        input.datepicker({"autoSize": true, "dateFormat": "dd/mm/yy", "onSelect": bag.persistField, "onClose": bag.restore});
        input.val(obj.text());
        bag.setInputControl(input);
        return input;
    }
    if (obj.hasClass('gendercontent'))
    {
        var html = "<div style='display:inline;' tabindex=1>";
        var items = ['Non specificato', 'Femmina', 'Maschio'];
        for (var i = 0; i < items.length; i++)
        {
            var item = items[i];
            var selected = item === obj.text();
            html += '<input type="radio" value="' + i + '"' + (selected ? ' checked' : '') + ' name="' + obj.attr('name') + '"/>' + item;
        }
        html += "</div>";
        var input = $(html);
        $('input', input).change(bag.persistField);
        bag.setInputControl(input);
        bag.getInputValue = function()
        {
            var index = $('input::checked', this.getInputControl()).val();
            return items[index];
        };
        return input;
    }
    var input = $("<input type='text'></input>");
    input.val(obj.text());
    input.blur(bag.persistField);
    bag.setInputControl(input);
    return input;
}
function editField()
{
    var obj = $(this);
    var input = getInput(obj);
    obj.replaceWith(input);
    input.focus();

}
function InputBag(objPar)
{
    var thisObj = this;
    var obj = objPar;
    var inputControl = null;
    this.setInputControl = function(inputPar)
    {
        inputControl = inputPar;
    };
    this.getInputControl = function()
    {
        return inputControl;
    }
    this.getInputValue = function()
    {
        return inputControl.val();
    };

    this.unformatData = function()
    {
        if (obj.hasClass('datecontent'))
        {
            var currentDate = $.datepicker.parseDate('dd/mm/yy', thisObj.getInputControl().val());
            return currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
        }
        if (obj.hasClass('gendercontent'))
        {
            return $('input::checked', thisObj.getInputControl()).val();
        }
        return inputControl.val();
    };
    this.persistField = function()
    {
        var value = thisObj.getInputValue();
        var modified = obj.text() !== value;
        obj.click(editField);
        inputControl.replaceWith(obj);
        if (modified)
        {
            var data = {};
            data[obj.attr("name")] = thisObj.unformatData();
            $.post("/user/update", data, function(res) {
                if (res && res.result)
                    obj.text(value);
            }, 'json');
        }
    };
    this.restore = function()
    {
        obj.click(editField);
        inputControl.replaceWith(obj);
    }
}