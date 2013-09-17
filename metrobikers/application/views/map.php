
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
    var google_api_key = 'AIzaSyBGO_LOR-sHoEPfdcVhplZvc-HjpxayfLY';
    document.writeln('<script src="http://maps.google.com/maps?file=api&v=2&sensor=false&key=' + google_api_key + '" type="text/javascript"><' + '/' + 'script>');</script>



<div id="MapContainer" style="margin-left: 0px; margin-right: 0px; margin-top: 0px; margin-bottom: 0px; margin-left: auto;margin-right: auto">
    <div id="gmap_div" style="width: 800px; height: 600px; margin: 0px; margin-right: auto; margin-left: auto;
         background-color: #F0F0F0; overflow: hidden;">
        <p align="center" style="font: 10px Arial;">
            Attendere il caricamento della mappa prego...</p>
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
    gv_options = [];
    // important variable names:
    gv_options.map_div = 'gmap_div'; // the name of the HTML "div" tag containing the map itself; usually 'gmap_div'
    // basic map parameters:
    gv_options.width = 1000; // width of the map, in pixels. MARCO: do not use for fullscreen
    gv_options.height = 700; // height of the map, in pixels. MARCO: do not use for fullscreen
    gv_options.full_screen = false; // true|false: should the map fill the entire page (or frame)?
    //gv_options.center = [44.523909, 9.0595785];  // [latitude,longitude] - be sure to keep the square brackets
    gv_options.zoom = 'auto'; // higher number means closer view; can also be 'auto'
    gv_options.map_opacity = 1; // number from 0 to 1
    gv_options.map_type = 'G_NORMAL_MAP'; // popular map_type choices are 'G_NORMAL_MAP', 'G_SATELLITE_MAP', 'G_HYBRID_MAP', 'G_PHYSICAL_MAP', 'MYTOPO_TILES'
    gv_options.doubleclick_zoom = false; // true|false: zoom in when mouse is double-clicked?
    gv_options.mousewheel_zoom = true; // true|false; or 'reverse' for down=in and up=out
    gv_options.centering_options = {'open_info_window': true, 'partial_match': true, 'center_key': 'center', 'default_zoom': null} // URL-based centering (e.g., ?center=name_of_marker&zoom=14)

    // widgets on the map:
    gv_options.zoom_control = 'large'; // 'large'|'small'|'3d'
    gv_options.scale_control = true; // true|false
    gv_options.center_coordinates = true; // true|false: show a "center coordinates" box and crosshair?
    gv_options.crosshair_hidden = true; // true|false: hide the crosshair initially?
    gv_options.map_opacity_control = true; // true|false
    gv_options.map_type_control = []; // widget to change the background map
    gv_options.map_type_control.style = 'menu'; // 'menu'|'list'|'none'|'google'
    gv_options.map_type_control.filter = true; // true|false: when map loads, are irrelevant maps ignored?
    //gv_options.map_type_control.excluded = ['G_SATELLITE_3D_MAP'];  // comma-separated list of map types that will never show in the list ('included' also works)
    gv_options.legend_options = []; // options for a floating legend box (id="gv_legend"), which can contain anything
    gv_options.legend_options.legend = true; // true|false: enable or disable the legend altogether
    gv_options.legend_options.position = ['G_ANCHOR_BOTTOM_LEFT', 4, 60]; // [Google anchor name, relative x, relative y]
    gv_options.legend_options.draggable = true; // true|false: can it be moved around the screen?
    gv_options.legend_options.collapsible = true; // true|false: can it be collapsed by double-clicking its top bar?
    gv_options.measurement_tools = {enabled: false, distance_color: 'blue', area_color: 'red', position: ['G_ANCHOR_BOTTOM_LEFT', 100, 60]};
    // track-related options:
    gv_options.tracklist_options = []; // options for a floating list of the tracks visible on the map
    gv_options.tracklist_options.tracklist = true; // true|false: enable or disable the tracklist altogether
    gv_options.tracklist_options.position = ['G_ANCHOR_TOP_RIGHT', 6, 32]; // [Google anchor name, relative x, relative y]
    gv_options.tracklist_options.max_width = 180; // maximum width of the tracklist, in pixels
    gv_options.tracklist_options.max_height = 610; // maximum height of the tracklist, in pixels; if the list is longer, scrollbars will appear
    gv_options.tracklist_options.desc = true; // true|false: should tracks' descriptions be shown in the list
    gv_options.tracklist_options.zoom_links = true; // true|false: should each item include a small icon that will zoom to that track?
    gv_options.tracklist_options.tooltips = true; // true|false: should the name of the track appear on the map when you mouse over the name in the list?
    gv_options.tracklist_options.draggable = true; // true|false: can it be moved around the screen?
    gv_options.tracklist_options.collapsible = true; // true|false: can it be collapsed by double-clicking its top bar?

    // marker-related options:
    gv_options.default_marker = {color: 'red', icon: 'googlemini'}; // icon can be a URL, but be sure to also include size:[w,h] and optionally anchor:[x,y]
    gv_options.shadows = true; // true|false: do the standard markers have "shadows" behind them?
    gv_options.marker_link_target = '_blank'; // the name of the window or frame into which markers' URLs will load
    gv_options.info_window_width = 0; // in pixels, the width of the markers' pop-up info "bubbles" (can be overridden by 'window_width' in individual markers)
    gv_options.thumbnail_width = 0; // in pixels, the width of the markers' thumbnails (can be overridden by 'thumbnail_width' in individual markers)
    gv_options.photo_size = [0, 0]; // in pixels, the size of the photos in info windows (can be overridden by 'photo_width' or 'photo_size' in individual markers)
    gv_options.hide_labels = false; // true|false: hide labels when map first loads?
    gv_options.label_offset = [0, 0]; // [x,y]: shift all markers' labels (positive numbers are right and down)
    gv_options.label_centered = false; // true|false: center labels with respect to their markers?  (label_left is also a valid option.)
    gv_options.driving_directions = false; // put a small "driving directions" form in each marker's pop-up window? (override with dd:true or dd:false in a marker's options)
    gv_options.garmin_icon_set = 'gpsmap'; // 'gpsmap' are the small 16x16 icons; change it to '24x24' for larger icons
    gv_options.marker_list_options = []; // options for a dynamically-created list of markers
    gv_options.marker_list_options.list = false; // true|false: enable or disable the marker list altogether
    gv_options.marker_list_options.floating = true; // is the list a floating box inside the map itself?
    gv_options.marker_list_options.id_static = 'gv_marker_list_static'; // id of a DIV that holds a non-floating list
    gv_options.marker_list_options.id_floating = 'gv_marker_list'; // id of a DIV tag that holds a floating list (other associated DIVs -- _handle, _table, _container -- must be similarly named)
    gv_options.marker_list_options.width = 160; // floating list only: width, in pixels
    gv_options.marker_list_options.height = 516; // floating list only: height, in pixels
    gv_options.marker_list_options.position = ['G_ANCHOR_BOTTOM_RIGHT', 6, 38]; // floating list only: position within map
    gv_options.marker_list_options.draggable = true; // true|false, floating list only: can it be moved around the screen?
    gv_options.marker_list_options.collapsible = true; // true|false, floating list only: can it be collapsed by double-clicking its top bar?
    gv_options.marker_list_options.include_tickmarks = false; // true|false: are distance/time tickmarks included in the list?
    gv_options.marker_list_options.include_trackpoints = true; // true|false: are "trackpoint" markers included in the list?
    gv_options.marker_list_options.dividers = false; // true|false: will a thin line be drawn between each item in the list?
    gv_options.marker_list_options.desc = false; // true|false: will the markers' descriptions be shown below their names in the list?
    gv_options.marker_list_options.icons = true; // true|false: should the markers' icons appear to the left of their names in the list?
    gv_options.marker_list_options.thumbnails = true; // true|false: should markers' thumbnails be shown in the list?
    gv_options.marker_list_options.folders_collapsed = false; // true|false: do folders in the list start out in a collapsed state?
    gv_options.marker_list_options.wrap_names = true; // true|false: should marker's names be allowed to wrap onto more than one line?
    gv_options.marker_list_options.unnamed = '[unnamed]'; // what 'name' should be assigned to  unnamed markers in the list?
    gv_options.marker_list_options.colors = false; // true|false: should the names/descs of the points in the list be colorized the same as their markers?
    gv_options.marker_list_options.default_color = ''; // default HTML color code for the names/descs in the list
    gv_options.marker_list_options.limit = 0; // how many markers to show in the list; 0 for no limit
    gv_options.marker_list_options.center = false; // true|false: does the map center upon a marker when you click its name in the list?
    gv_options.marker_list_options.zoom = false; // true|false: does the map zoom to a certain level when you click on a marker's name in the list?
    gv_options.marker_list_options.zoom_level = 17; // if 'zoom' is true, what level should the map zoom to?
    gv_options.marker_list_options.info_window = true; // true|false: do info windows pop up when the markers' names are clicked in the list?
    gv_options.marker_list_options.url_links = false; // true|false: do the names in the list become instant links to the markers' URLs?
    gv_options.marker_list_options.toggle = false; // true|false: does a marker disappear if you click on its name in the list?
    gv_options.marker_list_options.help_tooltips = false; // true|false: do "tooltips" appear on marker names that tell you what happens when you click?
    gv_options.marker_list_options.header = ''; // HTML code; be sure to put backslashes in front of any single quotes, and don't include any line breaks
    gv_options.marker_list_options.footer = ''; // HTML code; be sure to put backslashes in front of any single quotes, and don't include any line breaks
    gv_options.marker_filter_options = []; // options for removing waypoints that are out of the current view
    gv_options.marker_filter_options.filter = false; // true|false: should out-of-range markers be removed?
    gv_options.marker_filter_options.movement_threshold = 8; // in pixels, how far the map has to move to trigger filtering
    gv_options.marker_filter_options.limit = 0; // maximum number of markers to display on the map; 0 for no limit
    gv_options.marker_filter_options.update_list = true; // true|false: should the marker list be updated with only the filtered markers?
    gv_options.marker_filter_options.sort_list_by_distance = false; // true|false: should the marker list be sorted by distance from the center of the map?
    gv_options.marker_filter_options.min_zoom = 0; // below this zoom level, don't show any markers at all
    gv_options.marker_filter_options.zoom_message = ''; // message to put in the marker list if the map is below the min_zoom threshold

    //addCustomOptions();
    document.writeln('<script src="<?php echo base_url() ?>asset/js/map.js" type="text/javascript"><' + '/' + 'script>');</script>

<style type="text/css">
    /* Put any custom style definitions here (e.g., .gv_marker_info_window, .gv_marker_list_item, .gv_tooltip, .gv_label, etc.) */.gv_label
    {
        filter: alpha(opacity=80);
        -moz-opacity: 0.8;
        opacity: 0.8;
        background: #333333;
        border: 1px solid black;
        padding: 1px;
        font: 9px Verdana,sans-serif;
        color: white;
        font-weight: normal;
    }
</style>

<script type="text/javascript">

    function addTracks()
    {
<?php
if (isset($routes)) {
    $index = 0;
    foreach ($routes as $route) {
        ?>
                        t = <?php echo$index++; ?>;
                        track = [];
                        trk_info[t] = track;
                        track['name'] = '<?php echo $route->name; ?>';
                        track['desc'] = '<?php echo $route->name; ?>';
                        track['clickable'] = true;
                        track['width'] = 3;
                        track['opacity'] = 0.9;
                        track['outline_color'] = '#000000';
                        track['outline_width'] = 0;
                        track['fill_color'] = '#E60000';
                        track['fill_opacity'] = 0;

                        trkSeg = [];
                        trk_segments[t] = trkSeg;
        <?php
        for ($i = 0; $i < count($route->points) - 1; $i++) {
            $p1 = $route->points[$i];
            $p2 = $route->points[$i + 1];
            ?>
                                trkSeg.push({color: 'red', points: [[<?php echo $p1->lat / 1000000; ?>, <?php echo $p1->lon / 1000000; ?>], [<?php echo $p2->lat / 1000000; ?>, <?php echo $p2->lon / 1000000; ?>]]});
        <?php } ?>
                        GV_Draw_Track(t);
        <?php
    }
}
?>
    }
   
    function addMarkers(){
        jQuery.get("<?php echo base_url() ?>mobile/get_positions/0/0/6000000000/600000000", null, function(data) {
            if (!data)
                return;
            for (var i = 0; i < data.length; i++) {
                var obj = data[i];
                GV_Draw_Marker({ lat: obj.lat / 1000000, lon: obj.lon / 1000000, name: obj.name, desc: '', color: 'red' });
            }
        });
    }
    function GV_Map() {
        if (GBrowserIsCompatible()) {
            if (gv_options.full_screen) {
                GV_Fill_Window_With_Map(gv_options.map_div);
            }
            gmap = new GMap2(document.getElementById(gv_options.map_div)); // create map
            GV_Setup_Map(gv_options);
            addTracks();
            //GV_Add_Track_to_Tracklist({bullet:'- ',name:trk_info[t]['name'],desc:trk_info[t]['desc'],color:trk_info[t]['color'],number:t});
            addMarkers();

            if (google.loader.ClientLocation) {
                var marker = {};
                marker.lat = google.loader.ClientLocation.latitude;
                marker.lon = google.loader.ClientLocation.longitude;
                marker.name = "Sei qui";
                marker.color = "blue";
                GV_Draw_Marker(marker);
            }

            GV_Finish_Map(gv_options);
        } else {
            document.getElementById('gmap_div').style.backgroundColor = '#DDDDDD';
            document.getElementById('gmap_div').innerHTML = 'Spiacente, la mappa non può essere visualizzata.';
        }
    }
    google.load("maps", "3.x", {other_params: "sensor=false", callback: GV_Map});
    GV_Map(); // execute the above code
</script>


