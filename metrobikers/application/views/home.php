<!DOCTYPE html>
<html lang="en" ng-app="Home">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <p><b>«</b>&nbsp;Sono le azioni che contano. I nostri pensieri, per quanto 
            buoni possano essere, sono perle false fintanto che non vengono trasformati 
            in azioni. Sii il cambiamento che vuoi vedere nel mondo.&nbsp;<b>»</b>&nbsp;
            <small><a href="http://it.wikipedia.org/wiki/Mahatma_Gandhi">(Mahatma Gandhi)</a></small></p>
        <p>Spostarsi in auto in città spesso significa sopportare ore di coda, perdere 
            tempo alla ricerca di parcheggi, stress, costi di carburante, assicurazioni, 
            manutenzione, inquinamento.
        <p>Ridurre questi aspetti negativi contribuirebbe a migliorare la qualità della vita di ognuno di noi.</p>
        <p>Utilizzare la bicicletta é una possibile soluzione: tuttavia molte 
            città non sono ancora a misura di bicicletta e spesso ad utilizzare un 
            mezzo a due ruote sono ancora pochi temerari.</p>
        <p><b>ecommuters.com si prefigge di colmare questa lacuna</b>: registrati, trova persone che 
            percorrono in bicicletta il tuo stesso tragitto casa-lavoro ed unisciti a loro.
            Facendo massa critica saremo più rispettati dagli automobilisti, saremo più visibili 
            aumentando la sicurezza dello spostamento, potremo chiedere a gran voce alle amministrazioni 
            locali la creazione di percorsi ciclabili perchè proporremo i tragitti che percorriamo 
            tutti assieme tutti i giorni.</p>
        <p><b>Questa è la missione di ecommuters.com</b>: facilitare la ricerca di compagni di viaggio 
            casa-lavoro al fine di coinvolgere più persone possibile, verso un futuro in cui in città 
            saranno poche auto a transitare tra migliaia di biciclette. </p>
        <a href="/asset/other/ECOmmutersMobile.apk" >Scarica la App per Android</a>
        <p><em>Richiesta email per aggiornamenti</em></p>
        <p>Se interessati lasciare mail qui sotto</p>
        <div ng-controller="MailCollector">
            <form ng-submit="collectMail()">
                <input type="text" ng-model="emailAddress" placeholder="insert your email address">
                <input class="button" type="submit" value="ok">
            </form>
            <span>{{greetings}}</span>
        </div>
        <br />
        <br />
        <?php
        $data = isset($this->routes) ? array('routes'=> $this->routes) : array();
        $this->load->view('map', $data);
        ?>
        <?php $this->load->view('templates/footer'); ?>
        <script>angular.module("Home", []).value("base_url", "<?php echo base_url() ?>");</script>
        <script type="text/javascript" src="asset/js/mailCollector.js"></script>
    </body>
</html>