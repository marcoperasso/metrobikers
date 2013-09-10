<!DOCTYPE html>
<html lang="en" ng-app="Home">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
        <style>
            p
            {
                padding:0px 20px 0px 20px;
            }
        </style>
    </head>

    <body>
        <?php $this->load->view('templates/visualheader'); ?>
        <p style ="padding:0px 20px 0px 20px; margin-bottom: 0px;"><i>«Sono le azioni che contano. I nostri pensieri, per quanto 
                buoni possano essere, sono perle false fintanto che non vengono trasformati 
                in azioni. Sii il cambiamento che vuoi vedere nel mondo.»</i></p>
        <p style="text-align: right; padding-right: 50px; margin:0px;"><small><a target="gandhi" href="http://it.wikipedia.org/wiki/Mahatma_Gandhi">(Mahatma Gandhi)</a></small></p>
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
        <p>Ti riconosci in questi valori? Bene, <a href="register" title="Diventa un ECOmmuter!">diventa anche tu un ECOmmuter</a>!</p>
        <a href="/asset/other/ECOmmutersMobile.apk" >Scarica la App per Android</a>
        <br />
        <?php
        $data = isset($this->routes) ? array('routes' => $this->routes) : array();
        $this->load->view('map', $data);
        ?>
        <?php $this->load->view('templates/footer'); ?>
        <script>angular.module("Home", []).value("base_url", "<?php echo base_url() ?>");</script>
        <script type="text/javascript" src="asset/js/mailCollector.js"></script>
    </body>
</html>