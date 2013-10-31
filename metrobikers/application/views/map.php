<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>

<div id="mapcontainer" style="margin-left: 0px; margin-right: 0px; margin-top: 0px; margin-bottom: 0px; margin-left: auto;margin-right: auto">
    <div id="gmap_div" style="width: 80%; height: 700px; margin: 0px; margin-right: auto; margin-left: auto;
         background-color: #F0F0F0; overflow: hidden;">
        <p align="center" style="font: 10px Arial;">
            Per favore attendi, sto caricando la mappa...</p>
    </div>
    <div id="gv_legend_container" style="display: none;">
        <table id="gv_legend_table" style="position: relative; filter: alpha(opacity=95);
               -moz-opacity: 0.95; opacity: 0.95; background: #ffffff;" cellpadding="0" cellspacing="0"
               border="0">
            <tr>
                <td>
                    <div id="gv_legend_handle" align="center" style="height: 6px; max-height: 6px; background: #CCCCCC;
                         border-left: 1px solid #999999; border-top: 1px solid #EEEEEE; border-right: 1px solid #999999;
                         padding: 0px; cursor: move;">
                        <!-- -->
                    </div>
                    <div id="gv_legend" align="left" style="line-height: 13px; border: solid #000000 1px;
                         background: #FFFFFF; padding: 4px; font: 11px Arial;">
                        <div id="gv_legend_header" style="padding-bottom: 2px;">
                            <b>Altitudine (m)</b></div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div id="gv_tracklist_tooltip" class="gv_tracklist_tooltip" style="background-color: #FFFFFF;
         border: 1px solid #CCCCCC; padding: 2px; font: 11px Arial; display: none;">
    </div>
    <!-- the following is the "floating" marker list; the "static" version is below -->
    <div id="gv_marker_list_container" style="display: none;">
        <table id="gv_marker_list_table" style="position: relative; filter: alpha(opacity=95);
               -moz-opacity: 0.95; opacity: 0.95;" cellspacing="0" cellpadding="0" border="0">
            <tr>
                <td>
                    <div id="gv_marker_list_handle" align="center" style="height: 6px; max-height: 6px;
                         background: #CCCCCC; border-left: 1px solid #999999; border-top: 1px solid #EEEEEE;
                         border-right: 1px solid #999999; padding: 0px; cursor: move;">
                        <!-- -->
                    </div>
                    <div id="gv_marker_list" align="left" class="gv_marker_list" style="overflow: auto;
                         background: #FFFFFF; border: solid #666666 1px; padding: 4px;">
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div id="gv_marker_list_static" align="left" class="gv_marker_list" style="width: 160px;
         overflow: auto; float: left; display: none;">
    </div>
    <div id="gv_clear_margins" style="height: 0px; clear: both;">
        <!-- clear the "float" -->
    </div>
</div>

<script type="text/javascript">
    function RouteInfo(name, infoWindow)
    {
        var routeName = name;
        var window = infoWindow;
        this.onDetailRequested = function(e) {
            window.setContent(routeName);
            window.setPosition(e.latLng);
            window.open(map);
        };
    }
    function addTracks()
    {
        var points;
        var myInfoWindow = new google.maps.InfoWindow();

<?php

function get_gradient($startcol, $endcol, $graduations = 255) {
    $graduations--;

    $RedOrigin = hexdec(substr($startcol, 0, 2));
    $GrnOrigin = hexdec(substr($startcol, 2, 2));
    $BluOrigin = hexdec(substr($startcol, 4, 2));

    $GradientSizeRed = (hexdec(substr($endcol, 0, 2)) - $RedOrigin) / $graduations; //Graduation Size Red
    $GradientSizeGrn = (hexdec(substr($endcol, 2, 2)) - $GrnOrigin) / $graduations;
    $GradientSizeBlu = (hexdec(substr($endcol, 4, 2)) - $BluOrigin) / $graduations;

    for ($i = 0; $i <= $graduations; $i++) {
        $RetVal[$i] =
                str_pad(dechex($RedOrigin + ($GradientSizeRed * $i)), 2, '0', STR_PAD_LEFT) .
                str_pad(dechex($GrnOrigin + ($GradientSizeGrn * $i)), 2, '0', STR_PAD_LEFT) .
                str_pad(dechex($BluOrigin + ($GradientSizeBlu * $i)), 2, '0', STR_PAD_LEFT);
    }
    return $RetVal;
}

function get_color($ratio, $colors) {
    $index = ($ratio * (count($colors) - 1));
    return "#" . $colors[$index];
}

if (isset($routes)) {
    $colors = get_gradient("00FF00", "0000FF");
    foreach ($routes as $route) {
        ?>
                points = [];
        <?php
        $count = count($route->points);
        for ($i = 0; $i < $count; $i++) {
            $p1 = $route->points[$i];
            ?>
                    points.push({"lat":<?php echo $p1->lat / 1000000; ?>, "lon": <?php echo $p1->lon / 1000000; ?>, "c": '<?php echo get_color($i / $count, $colors); ?>'});
        <?php } ?>
                for (var i = 1; i < points.length; i++)
                {
                    var p1 = points[i - 1];
                    var p2 = points[i];

                    var poly = new google.maps.Polyline({
                        path: [new google.maps.LatLng(p1.lat, p1.lon), new google.maps.LatLng(p2.lat, p2.lon)],
                        strokeColor: p1.c,
                        strokeOpacity: 1.0,
                        strokeWeight: 3,
                        map: map
                    });
                    var obj = new RouteInfo("<?php echo $route->name; ?>", myInfoWindow);
                    
                    google.maps.event.addListener(poly, 'click', obj.onDetailRequested);


                }
        <?php
    }
}
?>
    }

    function refreshPositionMarkers() {
        for (i = 0; i < positions_markers.length; i++)
            positions_markers[i].setMap(null);
        positions_markers.length = 0;

        var bounds = map.getBounds();
        var NE = bounds.getNorthEast()
        var SW = bounds.getSouthWest();
        var url = "<?php echo base_url() ?>mobile/get_positions/" +
                Math.round(SW.lat() * 1000000) +
                "/" +
                Math.round(SW.lng() * 1000000) +
                "/" +
                Math.round(NE.lat() * 1000000) +
                "/" +
                Math.round(NE.lng() * 1000000);
        function zeroPad(num, places) {
            var zero = places - num.toString().length + 1;
            return Array(+(zero > 0 && zero)).join("0") + num;
        }
        jQuery.get(url, null, function(data) {
            if (!data)
                return;
            for (var i = 0; i < data.length; i++) {
                var obj = data[i];
                var date = new Date(obj.time * 1000);
                var marker = new google.maps.Marker({
                    position: new google.maps.LatLng(obj.lat / 1000000, obj.lon / 1000000),
                    title: obj.name + " " + obj.surname + " (" + zeroPad(date.getHours(), 2) + ":" + zeroPad(date.getMinutes(), 2) + ")",
                    icon: "/asset/img/routemarker.png"
                });
                marker.setMap(map);
                positions_markers.push(marker);
            }
        });
    }
    var map;
    var positions_markers = [];
    function initialize(lat, lon) {
        var mapOptions = {
            zoom: 8,
            center: new google.maps.LatLng(lat, lon),
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById('gmap_div'),
                mapOptions);

        addTracks();

        google.maps.event.addListener(map, 'bounds_changed', refreshPositionMarkers);
        setInterval(refreshPositionMarkers, 5000);



    }
    if (navigator.geolocation)
    {
        navigator.geolocation.getCurrentPosition(initializeLocation, initializeNoLocation);
    }
    else
    {
        initializeNoLocation();
    }

    function initializeLocation(position)
    {
        initialize(position.coords.latitude, position.coords.longitude);
    }
    function initializeNoLocation()
    {
        initialize(44.403373, 8.949738);
    }

</script>


