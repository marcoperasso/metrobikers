<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
            <div class="row">
                <div class="col-md-12">
                   <div class="row">
    <div class="col-md-3">

    </div>
    <div class="col-md-6">
        <a href="/" title="Vai alla pagina principale">
            <img src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo" class="logo">
        </a>
    </div>
    <div class="col-md-3">
        <a class ="logincommand" href="javascript:void(0)" title="Entra">
            <img class="headercommand" src="<?php echo base_url() ?>asset/img/enter.png" alt="Entra"/>
        </a>
        <a class ="registercommand" href="register" title="Diventa un ECOmmuter!">
            <img class="headercommand" src="<?php echo base_url() ?>asset/img/register.png" alt="Diventa un ECOmmuter!"/>
        </a>
    </div>
</div>
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
                </div>
            </div>
            <div class="row">
                <div class="col-md-1"></div>
                <div class="col-md-10">
                    <blockquote>
                    <p class="text-center">Sono le azioni che contano.<br />
                        I nostri pensieri, per quanto buoni possano essere, sono perle false fintanto che non vengono trasformati 
                            in azioni.<br />
                            <b>Sii il cambiamento che vuoi vedere nel mondo.</b></p>
                    <p class="text-center"><small><cite title="Mahatma Gandhi"><a target="gandhi" href="http://it.wikipedia.org/wiki/Mahatma_Gandhi">Mahatma Gandhi</a></cite></small></p>
                    </blockquote>
                    <p>Spostarsi in auto in città spesso significa sopportare ore di coda, perdere 
                        tempo alla ricerca di parcheggi, stress, costi di carburante, assicurazioni, 
                        manutenzione, inquinamento; ridurre questi aspetti negativi contribuirebbe a migliorare la qualità della vita di 
                        ognuno di noi. La vita sedentaria che spesso conduciamo ci obbliga inoltre a diete forzate per mantenerci in buona salute e
                        costose sedute di palestra.</p>
                    <p>La bicicletta è una possibile risposta a tutti questi problemi: tuttavia molte 
                        città non sono ancora a misura di ciclista e spesso ad utilizzare un 
                        mezzo a due ruote sono solo pochi temerari.</p>
                    <p><b>Con ECOmmuters cerchiamo di colmare questa lacuna</b>: registra i tuoi itinerari utilizzando l'applicazione per smartphone, trova persone che 
                        percorrono in bicicletta il tuo stesso tragitto casa-lavoro ed unisciti a loro.
                        Facendo massa critica saremo più rispettati dagli automobilisti, godremo di maggiore visibilità
                        aumentando la sicurezza dello spostamento, potremo chiedere a gran voce alle amministrazioni 
                        locali la creazione di percorsi ciclabili perchè proporremo i tragitti che percorriamo 
                        assieme tutti i giorni.</p>
                    <p><b>Questa è la missione di ECOmmuters</b>: facilitare la ricerca di compagni di viaggio 
                        al fine di coinvolgere più persone possibile per dare loro visibilità come singoli e come categoria, 
                        verso un futuro in cui in città 
                        saranno poche auto a transitare tra migliaia di biciclette. </p>
                    <br />
                    <p>Ti riconosci in questi valori? Allora fai il salto: <a href="register" class="btn btn-default">diventa anche tu un ECOmmuter!</a></p>
                    <br />
                    <br />
                    <br />
                    <br />
                    <br />
                    <br />
                    <a href="/asset/other/ECOmmutersMobile.apk" >Scarica la App per Android</a>
                    <br />
                    <?php
                    $data = isset($this->routes) ? array('routes' => $this->routes) : array();
                    $this->load->view('map', $data);
                    ?>
                    <?php $this->load->view('templates/footer'); ?>                
                </div>
                <div class="col-md-1"></div>
          </div>
    </body>
</html>
