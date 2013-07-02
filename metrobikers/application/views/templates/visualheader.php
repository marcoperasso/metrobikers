<img src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo" class="logo">
<?php
$CI = & get_instance();
$CI->load->library('session');
$user = $CI->session->userdata("user");
if ($user == NULL) {
    ?>
    <a class ="logincommand" href="javascript:void(0)" title="Entra">Entra</a>
<?php } else { ?>
    <span>Benvenuto, utente, appena riesco visualizzo il tuo nome! </span>
    <a class ="logoffcommand" href="javascript:void(0)" title="Esci">Esci</a>
<?php } ?>
<script type="text/javascript">
    function login()
    {
        var loginForm = $("#loginform");
        if (loginForm.length !== 0)
        {
            loginForm.dialog("open");
            return;
        }
        $.get("login", null, function(data) {
            loginForm = $(data)
                    .appendTo(document.body)
                    .dialog({
                autoOpen: true,
                width: 400,
                modal: true,
                buttons: {
                    Login: doLogin,
                    Cancel: function() {
                        $(this).dialog("close");
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