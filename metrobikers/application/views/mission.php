<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>Benvenuto in ECOmmuters</title>
    </head>

    <body>
        <div class="row header">
            <div class="col-md-4">
                <a href="<?php echo base_url() ?>" title="Vai alla pagina principale">
                    <img src="<?php echo base_url() ?>asset/img/logo.png" id="ecommuter_logo" class="logo">
                </a>
            </div>
            <div class="col-md-1"></div>
            <div class="col-md-6">
                <blockquote>
                    <br /><br /><br /><br /><br /><br /><br /><br /><br />
                    <p>Sono le azioni che contano.<br />
                        I nostri pensieri, per quanto buoni possano essere, sono perle false fintanto che non vengono trasformati 
                        in azioni.<br />
                        <b>Sii il cambiamento che vuoi vedere nel mondo.</b></p>
                    <p><small><cite title="Mahatma Gandhi"><a target="gandhi" href="http://it.wikipedia.org/wiki/Mahatma_Gandhi">Mahatma Gandhi</a></cite></small></p>
                </blockquote>
            </div>
            <div class="col-md-1"></div>
        </div>
        <div class="row body">
            <div class="col-md-1"></div>
            <div class="col-md-10">
                
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
                <p>Ti riconosci in questi valori? Allora fai il salto, <a href="<?php echo base_url() ?>register" class="btn btn-default">diventa anche tu un ECOmmuter!</a></p>
            </div>
            <div class="col-md-1"></div>
        </div>

        <?php $this->load->view('templates/footer'); ?>              
    </body>
</html>
