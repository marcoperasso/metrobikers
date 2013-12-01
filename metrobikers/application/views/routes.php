<script type="text/javascript">setActiveTab('routes');</script>
<?php
if (isset($routes) && count($routes)) {
    $this->load->view('map', array('routes' => $routes));
} else {
    ?>
    <div class="col-md-1"></div>
    <div class="col-md-10">
        <h1>Non ci sono itinerari</h1>
        <br>
        <br>
        <div class="row">
            <div class="col-md-5"><a title="Google Play" href ="https://plus.google.com/u/0/communities/102754557298986622823" target="play"><img src="/asset/img/googleplay.jpg"/></a></div>
            <div class="col-md-6 container">
                <h2>Entra a far parte della nostra <a href="https://plus.google.com/u/0/communities/102754557298986622823" title="Community Google+ ECOmmuters">community</a>, scarica l'applicazione per il tuo smartphone e registra i tuoi itinerari.</h2></div>
        </div>
    </div>
    <div class="col-md-1"></div>
    <?php
}
?>
            
