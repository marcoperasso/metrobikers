<div class="modal fade" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Accedi ad ECOmmuters</h4>
            </div>
            <div class="modal-body">
                <form id="loginform" autocomplete="on" method="post"> 
                    <fieldset>
                        <div id="mailValidatorScope">
                        <input type="text" name="email" id="loginemail" placeholder="Email" class="required form-control"/><br>
                        </div>
                        <input type="password" name="password" id="loginpassword" placeholder="Password" class="required form-control"/>
                        <br>
                        <p class="text-center"><small><a href="#" id="loginforget">Hai dimenticato la password?</a></small></p>
                    </fieldset>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="dologin">Accedi</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<script type="text/javascript" src="<?php echo base_url_considering_mobile() ?>asset/js/modal.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="<?php echo base_url_considering_mobile() ?>asset/js/md5-min.js"></script>
<script type="text/javascript">
    $("#loginforget").click(resetPassword);
    $("#dologin").click(doLogin);
    var _autologin = this.autologin;
    function doLogin()
    {
        if (testFields())
         doLoginInternal();
    }
    function doLoginInternal(onEnd)
    {
        jQuery.get("<?php echo base_url_considering_mobile() ?>crypt", null, function(data) {
            eval(data);
            var pwd = hex_md5($('#loginpassword').val());
            pwd = this.crypt(pwd);
            var jqr = $.getJSON("<?php echo base_url_considering_mobile() ?>login/dologin", {
                "email": $("#loginemail").val(),
                "pwd": pwd
            }, function(data) {
                if (onEnd)
                {
                    onEnd(data)
                }
                else
                {
                    if (!data.success)
                        alert(data.message);
                    else
                        window.location.href = "<?php echo base_url_considering_mobile() ?>user";
                }
            });
            jqr.fail(onFailRequest);
        }, "text");
    }
    function onFailRequest(jqXHR, textStatus, errorThrown) {
        alert(textStatus + ": " + errorThrown);
    }
    function resetPassword()
    {
        if (testFields($("#mailValidatorScope")) && confirm("Ti verrà inviata una mail all'indirizzo " + $("#loginemail").val() + " con le istruzioni per reimpostare la password; vuoi continuare?"))
        {
            var form = $(loginform);
            form.attr('action', '/register/reset_pwd');
            form.submit();
        }
    }
    if (_autologin)
    {
        $("#loginemail").val(_autologin.getUser());
        $("#loginpassword").val(_autologin.getPassword());
        try
        {
            doLoginInternal(function(data) {
                _autologin.completed(data.success, data.message);
            });
        }
        catch (e)
        {
            _autologin.completed(false, e);
        }
    }

</script>


