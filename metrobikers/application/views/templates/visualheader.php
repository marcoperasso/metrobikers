<table cellpadding="0" cellspacing="0" border="0" class ="heading">
    <tr>
        <td style="width:25%;"></td>
        <td style="width:50%;">
            <a href="/" title="Vai alla pagina principale">
                <img src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo" class="logo">
            </a>
        </td>
        <td style="width:25%;"><?php
            if (get_user() != NULL) {
                $CI = & get_instance();
                $CI->load->model('User_model');
                $user = unserialize($_SESSION['user']);
                ?>
                <span>Benvenuto, <?php echo $user->to_string(); ?></span>
                <a class ="logoffcommand" href="javascript:void(0)" title="Esci"><img class="headercommand" src="<?php echo base_url() ?>asset/img/exit.png" alt="Esci"/></a>

            <?php } else { ?>
                <a class ="logincommand" href="javascript:void(0)" title="Entra"><img class="headercommand" src="<?php echo base_url() ?>asset/img/enter.png" alt="Entra"/></a>
                <a class ="registercommand" href="register" title="Diventa un ECOmmuter!"><img class="headercommand" src="<?php echo base_url() ?>asset/img/register.png" alt="Diventa un ECOmmuter!"/></a>
            <?php } ?></td>

    </tr>
</table>
<script type="text/javascript">
    var loginForm = $("#loginform");
    function login()
    {
        if (loginForm.length !== 0)
        {
            loginForm.dialog("open");
            return;
        }
        $.get("login", null, function(data) {
            $(data).appendTo(document.body);
            loginForm = $("#loginform");
            loginForm.dialog({
                autoOpen: true,
                width: 400,
                modal: true,
                buttons: {
                    Login: doLogin,
                    Cancel: function() {
                        loginForm.dialog("close");
                    }
                },
                close: function() {

                }
            })
                    .keydown(function(e) {
                if (e.keyCode === 13)
                {
                    e.preventDefault();
                    e.stopPropagation();
                    doLogin();
                }
            });
        });
    }

    function logoff()
    {
        $.getJSON("login/dologoff", {}, function(data) {
            if (data.success === true)
                location.reload(true);
            else
                alert(data.message);
        });
    }
    $(".logincommand").click(login);
    $(".logoffcommand").click(logoff);

</script>