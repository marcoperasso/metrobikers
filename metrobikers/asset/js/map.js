/* FUNCTIONS FOR DRAWING GOOGLE MAPS WITH GPS VISUALIZER (http://www.gpsvisualizer.com/) */

google_api_version = gv_api_version = FindGoogleAPIVersion();
if (!self.GLatLng) {
    google_api_version = 0;
}
// if (window.location.toString().match(/debug/)) { alert ('google_api_key = '+google_api_key); }

function $(id) {
    return (document.getElementById(id));
}

if (self.gv_maptypecontrol_style) { // this indicates a pre-gv_options version of the code
    if ($('gv_credit') && $('gv_credit').innerHTML.indexOf('gpsvisualizer.com') < 0) {
        var credit_div = $('gv_credit')
        credit_div.style.font = '10px Verdana,sans-serif';
        credit_div.style.padding = '1px';
        credit_div.style.backgroundColor = '#ffffff';
        // credit_div.style.filter = 'alpha(opacity=80)'; credit_div.style.MozOpacity = 0.80; credit_div.style.KhtmlOpacity = 0.80;
        credit_div.innerHTML = '<b>Mappa creata con strumenti di <a target="_top" href="http://www.gpsvisualizer.com/">GPSVisualizer.com</a></b>';
    }
}

if (!self.gv_options) {
    GV_Setup_Global_Variables();
}

function GV_Setup_Global_Variables() {
    if (!self.gv_options) {
        gv_options = [];
    }
    if (!gv_options['map'] || typeof(gv_options['map']) == 'function') {
        gv_options['map'] = 'gmap';
    }

    // Define parameters of different marker types
    if (!self.gv_icons) {
        gv_icons = [];
    }
    gv_icons['circle'] = {is: [11, 11], ia: [5, 5], ss: [13, 13], iwa: [10, 2], isa: [5, 9], im: [0, 0, 10, 0, 10, 10, 0, 10, 0, 0], letters: true};
    gv_icons['pin'] = {is: [15, 26], ia: [7, 25], ss: [30, 26], iwa: [7, 1], isa: [12, 16], im: [5, 25, 5, 15, 2, 13, 1, 12, 0, 10, 0, 5, 1, 2, 2, 1, 4, 0, 10, 0, 12, 1, 13, 2, 14, 4, 14, 10, 13, 12, 12, 13, 9, 15, 9, 25, 5, 25], letters: true};
    gv_icons['square'] = {is: [11, 11], ia: [5, 5], ss: [13, 13], iwa: [10, 2], isa: [5, 9], im: [0, 0, 10, 0, 10, 10, 0, 10, 0, 0], letters: true};
    gv_icons['triangle'] = {is: [13, 13], ia: [6, 6], ss: [15, 15], iwa: [11, 3], isa: [6, 10], im: [0, 11, 6, 0, 12, 11, 0, 11], letters: false};
    gv_icons['diamond'] = {is: [13, 13], ia: [6, 6], ss: [13, 13], iwa: [11, 3], isa: [6, 10], im: [6, 0, 12, 6, 6, 12, 0, 6, 6, 0], letters: false};
    gv_icons['star'] = {is: [19, 19], ia: [9, 9], ss: [19, 19], iwa: [12, 5], isa: [13, 15], im: [9, 1, 17, 7, 14, 17, 4, 17, 1, 7, 9, 1], letters: false};
    gv_icons['cross'] = {is: [13, 13], ia: [6, 6], ss: [15, 15], iwa: [11, 3], isa: [6, 10], im: [4, 0, 8, 0, 8, 4, 12, 4, 12, 8, 8, 8, 8, 12, 4, 12, 4, 8, 0, 8, 0, 4, 4, 4, 4, 0], letters: false};
    gv_icons['airport'] = {is: [17, 17], ia: [8, 8], ss: [19, 19], iwa: [13, 3], isa: [13, 17], im: [6, 0, 10, 0, 16, 6, 16, 10, 10, 16, 6, 16, 0, 10, 0, 6, 6, 0], letters: false};
    gv_icons['google'] = {is: [20, 34], ia: [10, 33], ss: [37, 34], iwa: [9, 2], isa: [18, 25], im: [8, 33, 8, 23, 1, 13, 1, 6, 6, 1, 13, 1, 18, 6, 18, 13, 11, 23, 11, 33, 8, 33], letters: true};
    gv_icons['googleblank'] = {is: [20, 34], ia: [10, 33], ss: [37, 34], iwa: [15, 2], isa: [18, 25], im: [8, 33, 8, 23, 1, 13, 1, 6, 6, 1, 13, 1, 18, 6, 18, 13, 11, 23, 11, 33, 8, 33], letters: true};
    gv_icons['googlemini'] = {is: [12, 20], ia: [6, 20], ss: [22, 20], iwa: [5, 1], isa: [10, 15], im: [4, 19, 4, 14, 0, 7, 0, 3, 4, 0, 7, 0, 11, 3, 11, 7, 7, 14, 7, 19, 4, 19], letters: true};
    gv_icons['blankcircle'] = {is: [64, 64], ia: [32, 32], ss: [70, 70], iwa: [55, 8], isa: [31, 63], im: [19, 3, 44, 3, 60, 19, 60, 44, 44, 60, 19, 60, 3, 44, 3, 19, 19, 3], letters: false};
    gv_icons['camera'] = {is: [17, 13], ia: [8, 6], ss: [19, 15], iwa: [13, 3], isa: [13, 10], im: [1, 3, 6, 1, 10, 1, 15, 3, 15, 11, 1, 11, 1, 3], letters: false};
    gv_icons['tickmark'] = {is: [13, 13], ia: [6, 6], ss: [], iwa: [11, 3], isa: [], im: [6, 0, 12, 6, 6, 12, 0, 6, 6, 0], letters: false};

    gv_named_html_colors = GV_Define_Named_Colors();

    // default marker options
    if (!gv_options['default_marker'] && self.default_icon_style) { // very old code would use this
        gv_options['default_marker'] = [];
        gv_options['default_marker']['icon'] = (self.default_icon_style) ? default_icon_style : 'googlemini';
        gv_options['default_marker']['color'] = (self.default_icon_color) ? default_icon_color : 'red';
    } else if (!gv_options['default_marker'] && self.gv_marker_icon) { // old-but-not-QUITE-as-old code would use this
        gv_options['default_marker'] = [];
        gv_options['default_marker']['icon'] = (self.gv_marker_icon) ? gv_marker_icon : 'googlemini';
        gv_options['default_marker']['color'] = (self.gv_marker_color) ? gv_marker_color : 'red';
    } else if (!gv_options['default_marker']) {
        gv_options['default_marker'] = [];
        gv_options['default_marker']['icon'] = (self.gv_default_marker && gv_default_marker['icon']) ? gv_default_marker['icon'] : 'googlemini';
        gv_options['default_marker']['color'] = (self.gv_default_marker && gv_default_marker['color']) ? gv_default_marker['color'] : 'red';
        gv_options['default_marker']['size'] = (self.gv_default_marker && gv_default_marker['size']) ? gv_default_marker['size'] : null;
        gv_options['default_marker']['anchor'] = (self.gv_default_marker && gv_default_marker['anchor']) ? gv_default_marker['anchor'] : null;
        gv_options['default_marker']['transparent'] = (self.gv_default_marker && gv_default_marker['transparent']) ? gv_default_marker['transparent'] : null;
        gv_options['default_marker']['imagemap'] = (self.gv_default_marker && gv_default_marker['imagemap']) ? gv_default_marker['imagemap'] : null;
        gv_options['default_marker']['scale'] = (self.gv_default_marker && gv_default_marker['scale']) ? gv_default_marker['scale'] : null;
    }
    gv_options['default_marker']['color'] = gv_options['default_marker']['color'].replace(/^#/, '');
    gv_options['marker_link_target'] = (gv_options['marker_link_target']) ? gv_options['marker_link_target'] : ((self.marker_link_target) ? marker_link_target : '');
    gv_options['info_window_width'] = (gv_options['info_window_width']) ? gv_options['info_window_width'] : ((self.gv_max_info_window_width) ? gv_max_info_window_width : null);
    gv_options['driving_directions'] = (gv_options['driving_directions']) ? gv_options['driving_directions'] : ((self.gv_driving_directions) ? gv_driving_directions : false);
    gv_options['label_offset'] = (gv_options['label_offset'] && gv_options['label_offset'].length >= 2) ? gv_options['label_offset'] : ((self.gv_label_offset && self.gv_label_offset.length >= 2) ? gv_label_offset : [0, 0]);
    gv_options['label_centered'] = (gv_options['label_centered']) ? gv_options['label_centered'] : ((self.gv_label_centered) ? gv_label_centered : false);
    gv_options['hide_labels'] = (gv_options['hide_labels']) ? gv_options['hide_labels'] : false;

    // map control options
    if (!gv_options['map_type_control']) { // very old code would use this
        gv_options['map_type_control'] = [];
        gv_options['map_type_control']['style'] = (self.maptypecontrol_style) ? maptypecontrol_style : 'menu';
        gv_options['map_type_control']['filter'] = (self.filter_map_types) ? true : false;
    }
    gv_options['map_opacity'] = (gv_options['map_opacity']) ? gv_options['map_opacity'] : ((self.gv_bg_opacity) ? gv_bg_opacity : 1);

    // miscellaneous options
    gv_options['icon_directory'] = (gv_options['icon_directory']) ? gv_options['icon_directory'] : ((self.gv_icon_directory) ? gv_icon_directory : 'http://maps.gpsvisualizer.com/google_maps/icons/');
    gv_options['icon_directory'] = gv_options['icon_directory'].replace(/http:\/\/www\.gpsvisualizer\.com\/google_maps\//, 'http://maps.gpsvisualizer.com/google_maps/');

    gv_garmin_icons = GV_Define_Garmin_Icons(gv_options['icon_directory'], gv_options['garmin_icon_set']);


    // Some stuff related to marker lists
    gv_marker_list_exists = null;
    gv_marker_list_html = '';
    gv_marker_list_folders = [];
    gv_marker_list_count = 0;
    gv_marker_list_div = null;
    gv_marker_list_div_name = null;
    gv_marker_list_map_name = null;
    gv_marker_array_name = (self.gv_marker_list_options && gv_marker_list_options['array']) ? gv_marker_list_options['array'] : 'wpts';
    gv_marker_list_text_tooltip = '';
    gv_marker_list_icon_tooltip = '';
    if (self.gv_marker_list_options) {
        gv_marker_list_html = '';
        gv_marker_list_folders = [];
        gv_marker_list_count = 0;
        gv_marker_list_map_name = (gv_marker_list_options['map'] && typeof(gv_marker_list_options['map']) !== 'function') ? gv_marker_list_options['map'] : 'gmap';
        if (gv_marker_list_options['id']) { // compatibility with version that had only 'id'
            gv_marker_list_options['div'] = gv_marker_list_options['id'];
        } else if (gv_marker_list_options['floating'] === false || gv_marker_list_options['floating'] === true) { // check for presence of "floating" parameter
            if (gv_marker_list_options['floating'] === false) {
                gv_marker_list_options['div'] = (gv_marker_list_options['id_static']) ? gv_marker_list_options['id_static'] : 'gv_marker_list';
            } else {
                gv_marker_list_options['div'] = (gv_marker_list_options['id_static']) ? gv_marker_list_options['id_floating'] : 'gv_marker_list';
            }
        }
        if ($(gv_marker_list_options['div'])) {
            gv_marker_list_div_name = gv_marker_list_options['div'];
            gv_marker_list_div = $(gv_marker_list_div_name);
        } else if ($('gv_marker_list')) {
            gv_marker_list_div_name = 'gv_marker_list';
            gv_marker_list_div = $(gv_marker_list_div_name);
        } else {
            gv_marker_list_div_name = '';
            gv_marker_list_div = null;
        }
        if (gv_marker_list_div) {
            if (gv_marker_list_options['list'] === false) { // this trumps everything
                gv_marker_list_div.style.display = 'none';
                gv_marker_list_exists = false;
            } else if (gv_marker_list_div.style.display == '' || gv_marker_list_options['list'] == true) {
                gv_marker_list_div.style.display = 'block';
                gv_marker_list_exists = true;
            } else {
                gv_marker_list_exists = false;
            }
        } else {
            gv_marker_list_exists = false;
        }
    }

    // Some stuff related to filtering waypoints
    gv_filter_waypoints = null;
    gv_filter_marker_list = null;
    if (self.gv_marker_filter_options) {
        gv_filter_marker_list = (gv_marker_filter_options['update_list']) ? true : false;
        gv_filter_waypoints = (gv_marker_filter_options['filter']) ? true : false;
        if (gv_marker_filter_options['dynamic_marker_options'] && gv_marker_filter_options['dynamic_marker_options']['url']) {
            gv_marker_filter_options['filter'] = false;
            gv_filter_waypoints = false;
        }
    }

    // Create a default icon for all markers
    gv_default_icon_object = new GIcon();
    if (gv_options['default_marker']['icon'].indexOf('/') > -1) {
        gv_default_icon_object.image = gv_options['default_marker']['icon'].replace(/^c:\//, 'file:///c:/'); // fix local Windows file names
        gv_default_icon_object.iconSize = (gv_options['default_marker']['size'] && gv_options['default_marker']['size'][0] && gv_options['default_marker']['size'][1]) ? new GSize(gv_options['default_marker']['size'][0], gv_options['default_marker']['size'][1]) : new GSize(32, 32);
        gv_default_icon_object.iconAnchor = (gv_options['default_marker']['anchor'] && gv_options['default_marker']['anchor'][0] != null && gv_options['default_marker']['anchor'][1] != null) ? new GPoint(gv_options['default_marker']['anchor'][0], gv_options['default_marker']['anchor'][1]) : new GPoint(gv_default_icon_object.iconSize.width * 0.5, gv_default_icon_object.iconSize.height * 0.5);
        gv_default_icon_object.transparent = (gv_options['default_marker']['transparent'] && gv_options['default_marker']['transparent'].indexOf('/') > -1) ? gv_options['default_marker']['transparent'] : null;
        gv_default_icon_object.imageMap = (gv_options['default_marker']['imagemap'] && gv_options['default_marker']['imagemap'].length > 5) ? gv_options['default_marker']['imagemap'] : null; // [ 0,0, 0,gv_default_icon_object.iconSize.height-1, gv_default_icon_object.iconSize.width-1,gv_default_icon_object.iconSize.height-1, gv_default_icon_object.iconSize.width-1,0, 0,0 ];
        gv_default_icon_object.infoWindowAnchor = new GPoint(gv_default_icon_object.iconSize.width * 0.75, 0);
        gv_default_icon_object.infoShadowAnchor = new GPoint(gv_default_icon_object.iconSize.width * 0.5, gv_default_icon_object.iconSize.height);
        gv_default_icon_object.shadow = null;
        gv_default_icon_object.shadowSize = null;
        gv_icons[gv_options['default_marker']['icon']] = {is: [gv_default_icon_object.iconSize.width, gv_default_icon_object.iconSize.height], ia: [gv_default_icon_object.iconAnchor.x, gv_default_icon_object.iconAnchor.y], ss: null, iwa: [gv_default_icon_object.infoWindowAnchor.width, gv_default_icon_object.infoWindowAnchor.height], isa: [gv_default_icon_object.infoShadowAnchor.width, gv_default_icon_object.infoShadowAnchor.height], im: gv_default_icon_object.imageMap};
    } else {
        if (!gv_icons[gv_options['default_marker']['icon']]) {
            gv_options['default_marker']['icon'] = 'googlemini';
        }
        gv_default_icon_object.image = gv_options['icon_directory'] + gv_options['default_marker']['icon'] + '/' + gv_options['default_marker']['color'].toLowerCase() + '.png';
        gv_default_icon_object.transparent = gv_options['icon_directory'] + gv_options['default_marker']['icon'] + '/' + gv_options['default_marker']['color'].toLowerCase() + '-t.png';
        gv_default_icon_object.iconSize = new GSize(gv_icons[gv_options['default_marker']['icon']]['is'][0], gv_icons[gv_options['default_marker']['icon']]['is'][1]);
        gv_default_icon_object.iconAnchor = new GPoint(gv_icons[gv_options['default_marker']['icon']]['ia'][0], gv_icons[gv_options['default_marker']['icon']]['ia'][1]);
        gv_default_icon_object.infoWindowAnchor = new GPoint(gv_icons[gv_options['default_marker']['icon']]['iwa'][0], gv_icons[gv_options['default_marker']['icon']]['iwa'][1]);
        if (gv_options['marker_shadows'] === false) {
            gv_default_icon_object.shadow = null;
            gv_default_icon_object.shadowSize = null;
            gv_default_icon_object.infoShadowAnchor = null;
        } else {
            gv_default_icon_object.shadow = (gv_icons[gv_options['default_marker']['icon']]['ss'] && gv_icons[gv_options['default_marker']['icon']]['ss'][0]) ? gv_options['icon_directory'] + gv_options['default_marker']['icon'] + '/shadow.png' : null;
            gv_default_icon_object.shadowSize = (gv_icons[gv_options['default_marker']['icon']]['ss'] && gv_icons[gv_options['default_marker']['icon']]['ss'][0]) ? new GSize(gv_icons[gv_options['default_marker']['icon']]['ss'][0], gv_icons[gv_options['default_marker']['icon']]['ss'][1]) : null;
            gv_default_icon_object.infoShadowAnchor = new GPoint(gv_icons[gv_options['default_marker']['icon']]['isa'][0], gv_icons[gv_options['default_marker']['icon']]['isa'][1]);
        }
        if (gv_icons[gv_options['default_marker']['icon']]['im']) {
            gv_default_icon_object.imageMap = []; // it must be copied one item at a time or there's a telepathic connection between gv_icons[icon] and gv_default_icon_object!
            for (var i = 0; i < gv_icons[gv_options['default_marker']['icon']]['im'].length; i++) {
                gv_default_icon_object.imageMap[i] = gv_icons[gv_options['default_marker']['icon']]['im'][i];
            }
        } else {
            gv_default_icon_object.imageMap = [0, 0, 0, gv_icons[gv_options['default_marker']['icon']]['is'][1] - 1, gv_icons[gv_options['default_marker']['icon']]['is'][0] - 1, gv_icons[gv_options['default_marker']['icon']]['is'][1] - 1, gv_icons[gv_options['default_marker']['icon']]['is'][0] - 1, 0, 0, 0];
        }
    }

    if (gv_options['default_marker']['scale'] && gv_options['default_marker']['scale'] != 1) {
        var sc = gv_options['default_marker']['scale'];
        gv_default_icon_object.iconSize.width *= sc;
        gv_default_icon_object.iconSize.height *= sc;
        gv_default_icon_object.iconAnchor.x *= sc;
        gv_default_icon_object.iconAnchor.y *= sc;
        gv_default_icon_object.infoWindowAnchor = new GPoint(gv_default_icon_object.iconSize.width * 0.75, 0);
        gv_default_icon_object.infoShadowAnchor = new GPoint(gv_default_icon_object.iconSize.width * 0.5, gv_default_icon_object.iconSize.height);
        if (gv_default_icon_object.shadow) {
            gv_default_icon_object.shadowSize.width *= sc;
            gv_default_icon_object.shadowSize.height *= sc;
        }
        if (gv_default_icon_object.imageMap) {
            for (var i = 0; i < gv_default_icon_object.imageMap.length; i++) {
                gv_default_icon_object.imageMap[i] *= sc;
            }
        }
    }

}

GV_Styles();
function GV_Styles() {
    // Set up some styles
    document.writeln('		<style type="text/css">');
    document.writeln('			img.gv_marker_thumbnail { display:block; text-decoration:none; margin:0px; }');
    document.writeln('			img.gv_marker_photo { text-decoration:none; margin:0px; }');
    document.writeln('			.gv_tooltip { background-color:#ffffff; filter:alpha(opacity=100); -moz-opacity:1.0; opacity:1; border:1px solid #666666; padding:2px; text-align:left; font:10px Verdana,sans-serif; color:black; white-space:nowrap; }');
    document.writeln('			.gv_tooltip img.gv_marker_thumbnail { display:block; padding-top:3px; }');
    document.writeln('			.gv_tooltip img.gv_marker_photo { display:none; }');
    document.writeln('			.gv_center_coordinates { background-color:#ffffff; border:solid #666666 1px; padding:1px; font-family:Arial; font-size:10px; line-height:11px; }');
    document.writeln('			.gv_mouse_coordinates { background-color:#ffffff; border:solid #666666 1px; padding:1px; font-family:Arial; font-size:10px; line-height:11px; }');
    document.writeln('			.gv_track_tooltip { border:none; filter:alpha(opacity=80); -moz-opacity:0.8; opacity:0.8; }');
    document.writeln('			.gv_label { filter:alpha(opacity=90); -moz-opacity:0.9; opacity:0.9; background:#333333; border:1px solid black; padding:1px; text-align:left; white-space:nowrap; font:9px Verdana,sans-serif; color:white; }');
    document.writeln('			.gv_marker_info_window { font: 10px Verdana,sans-serif; padding-top:6px; }');
    document.writeln('			.gv_marker_info_window_name { font-weight:bold; padding-bottom:4px; margin-bottom:8px;}');
    document.writeln('			.gv_marker_info_window_desc { padding-top:0px; display:inline;}');
    document.writeln('			.gv_marker_info_window_photo { display:inline; }');
    document.writeln('			.gv_marker_info_window img.gv_marker_thumbnail { border-width:1px; margin:6px 0px 6px 0px; }');
    document.writeln('			.gv_driving_directions { background-color:#eeeeee; padding:4px; margin-top:12px; }');
    document.writeln('			.gv_driving_directions_heading { color:#666666; font-weight:bold; }');
    document.writeln('			.gv_label { filter:alpha(opacity=80); -moz-opacity:0.8; opacity:0.8; background:#333333; border:1px solid black; padding:1px; text-align:left; white-space: nowrap; font:9px Verdana,sans-serif; color:white; }');
    document.writeln('			.gv_label img { display:none; }');
    document.writeln('			.gv_legend_item { padding-bottom:0px; line-height:1.1em; font-weight:bold; }');
    document.writeln('			.gv_tracklist_item { padding-top:1px; padding-bottom:2px; }');
    document.writeln('			.gv_tracklist_item_name { font-weight:bold; font-size:11px; cursor:pointer; }');
    document.writeln('			.gv_tracklist_item_desc { font-weight:normal; font-size:11px; padding-top:2px; }');
    document.writeln('			.gv_marker_list { font-family:Verdana,sans-serif; font-size:10px; }');
    document.writeln('			.gv_marker_list_item { font-family:Verdana,sans-serif; font-size:10px; line-height:1.2em; padding:2px 0px 4px 0px; }');
    document.writeln('			.gv_marker_list_item_icon { cursor:pointer; float:left; margin-right:4px; margin-bottom:1px; }');
    document.writeln('			.gv_marker_list_item_name { cursor:pointer; font-weight:bold; }');
    document.writeln('			.gv_marker_list_item_desc { padding-top:2px; font-size:90%; }');
    document.writeln('			.gv_marker_list_border_top { border-top:1px solid #cccccc; }');
    document.writeln('			.gv_marker_list_border_bottom { border-bottom:1px solid #cccccc; }');
    document.writeln('			.gv_marker_list_thumbnail { padding-top:3px; border-width:1px; display:none; }');
    document.writeln('			.gv_marker_list_thumbnail img { border-width:1px; }');
    document.writeln('			.gv_marker_list_folder { padding-bottom:4px; }');
    document.writeln('			.gv_marker_list_folder_header { font-size:11px; font-weight:bold; padding-bottom:2px; background-color:#e4e4e4; }');
    document.writeln('			.gv_marker_list_folder_name { background-color:#e4e4e4; }');
    document.writeln('			.gv_marker_list_folder_contents { padding-left:15px; background-color:#ffffff; }');
    document.writeln('			.gv_maptypelink { background-color:#dddddd; color:#000000; text-align:center; white-space: nowrap; border:1px solid; border-color: #999999 #222222 #222222 #999999; padding:1px 2px 1px 2px; margin-bottom:3px; font:9px Verdana,sans-serif; text-decoration:none; cursor:pointer; }');
    document.writeln('			.gv_maptypelink_selected { background-color:#ffffff; }');
    document.writeln('			.gv_opacity_screen { background-color:#ffffff; }');
    document.writeln('		</style>');
    document.writeln('		<style type="text/css" media="print">'); // force stuff to print even though Google thinks it shouldn't
    document.writeln('			img[src^="http://www.gpsvisualizer.com/"].gmnoprint { display:inline; }'); // anything GV puts up
    document.writeln('			img[src^="http://maps.gpsvisualizer.com/"].gmnoprint { display:inline; }'); // anything GV puts up
    document.writeln('			img[src^="http://mt.google.com/mld"].gmnoprint { display:inline; }'); // Google Maps tracks
    document.writeln('			img[src$="transparent.png"].gmnoprint { display:none; }');
    document.writeln('			img[src$="shadow.png"].gmnoprint { display:none; }');
    document.writeln('			img[src$="crosshair.gif"].gmnoprint { display:none; }');
    document.writeln('			#gv_center_coordinates { display:none; }');
    document.writeln('			#gv_measurement_icon { display:none; }');
    document.writeln('			#gv_mouse_coordinates { display:none; }');
    document.writeln('			#gv_credit { display:none; }');
    document.writeln('		</style>');
}

// This must be done here or Google might get confused and not fill the map properly:
if (gv_options) {
    gv_options['map_div'] = (gv_options['map_div'] && $(gv_options['map_div'])) ? gv_options['map_div'] : 'gmap_div';
    if ($(gv_options['map_div']) && (gv_options['width'] || gv_options['height'])) {
        if (gv_options['width']) {
            $(gv_options['map_div']).style.width = parseFloat(gv_options['width']) + 'px';
        }
        if (gv_options['height']) {
            $(gv_options['map_div']).style.height = parseFloat(gv_options['height']) + 'px';
        }
    }
}

function GV_Setup_Map(opts) { // opts = options hash
    if (!opts && !self.gv_options) {
        return false;
    } else if (!opts && self.gv_options) {
        opts = gv_options;
    }
    if (!opts['map'] || typeof(opts['map']) == 'function') {
        opts['map'] = 'gmap';
    }
    var map_name = opts['map'];
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    var marker_array_name = (opts['marker_array']) ? opts['marker_array'] : 'wpts';
    var track_array_name = (opts['track_array']) ? opts['track_array'] : 'trk';
    var track_info_array_name = (opts['track_info_array']) ? opts['track_info_array'] : 'trk_info';
    var track_segments_array_name = (opts['track_segments_array']) ? opts['track_segments_array'] : 'trk_segments';

    // Variables that used to be separately specified and may be needed by other functions:
    gv_default_marker = opts['default_marker'];
    gv_options['icon_directory'] = opts['icon_directory'];
    gv_marker_filter_options = opts['marker_filter_options'];
    if (opts['marker_list_options']) {
        gv_marker_list_options = opts['marker_list_options'];
    }
    else {
        gv_marker_list_options = [];
    } // it needs to be created or the next statements will throw errors
    gv_marker_list_options['map'] = map_name;
    gv_marker_list_options['array'] = marker_array_name;
    gv_name_of_unnamed_marker = (gv_options && gv_options['marker_list_options'] && typeof(gv_options['marker_list_options']['unnamed']) != 'undefined') ? gv_options['marker_list_options']['unnamed'] : '[unnamed]';

    if (opts['full_screen']) {
        document.body.style.margin = '0px';
        document.body.style.overflow = 'hidden';
        GV_Fill_Window_With_Map(map.getContainer().id); // we may have already done this, and will probably do it again, but it doesn't hurt anything
        map.getContainer().style.margin = '0px';
        gv_marker_list_options['floating'] = true;
    } else {
        if (opts['width']) {
            map.getContainer().style.width = parseFloat(opts['width']) + 'px';
        }
        if (opts['height']) {
            map.getContainer().style.height = parseFloat(opts['height']) + 'px';
            if ($('gv_marker_list_static') || (opts['marker_list_options'] && opts['marker_list_options']['id_static'] && $(opts['marker_list_options']['id_static']))) {
                var marker_list_id = ($('gv_marker_list_static')) ? 'gv_marker_list_static' : opts['marker_list_options']['id_static'];
                if ($(marker_list_id).style && !$(marker_list_id).style.height) {
                    $(marker_list_id).style.height = parseFloat(opts['height']) + 'px';
                }
            }
        }
        map.checkResize();
    }

    GV_Setup_Global_Variables(); // this is the part of functions.js that, in the absence of gv_options, would have already happened (GV_Styles HAS been run, though)

    if (opts['doubleclick_zoom'] === false) {
        map.disableDoubleClickZoom();
    }

    if (opts['mousewheel_zoom']) {
        if (opts['mousewheel_zoom'] == 'reverse') {
            GEvent.addDomListener($(opts['map_div']), "DOMMouseScroll", GV_MouseWheelReverse); // mouse-wheel zooming for Firefox
            GEvent.addDomListener($(opts['map_div']), "mousewheel", GV_MouseWheelReverse); // mouse-wheel zooming for IE
        } else {
            GEvent.addDomListener($(opts['map_div']), "DOMMouseScroll", GV_MouseWheel); // mouse-wheel zooming for Firefox
            GEvent.addDomListener($(opts['map_div']), "mousewheel", GV_MouseWheel); // mouse-wheel zooming for IE
        }
    }

    if (typeof(opts['zoom']) == 'undefined') {
        opts['zoom'] = 'auto';
    }
    if (opts['zoom'] == 'auto' || (!opts['zoom'] && opts['zoom'] != '0')) {
        if (!opts['center'] || (opts['center'] && opts['center'].length == 0)) {
            opts['center'] = [40, -100];
        }
        map.setCenter(new GLatLng(opts['center'][0], opts['center'][1]), 8, eval(opts['map_type'] || 'G_NORMAL_MAP')); // temporary center
    } else {
        map.setCenter(new GLatLng(opts['center'][0], opts['center'][1]), opts['zoom'], eval(opts['map_type'] || 'G_NORMAL_MAP'));
    }

    if (opts['map_type_control'] && opts['map_type_control']['style'] && opts['map_type_control']['style'] == 'google') {
        map.addControl(gv_maptypecontrol = new GV_MapTypeControl(opts['map_type_control'])); // adds our custom map types
        map.removeControl(gv_maptypecontrol);
        map.addControl(gv_maptypecontrol = new GMenuMapTypeControl());
    } else if (opts['map_type_control'] && opts['map_type_control']['style'] && opts['map_type_control']['style'] != 'none') {
        map.addControl(gv_maptypecontrol = new GV_MapTypeControl(opts['map_type_control']));
    }
    if (opts['map_opacity_control']) {
        map.addControl(gv_mapopacitycontrol = new GV_MapOpacityControl(opts['map_opacity'])); // add custom map opacity control
    } else if (opts['map_opacity'] != 1 && opts['map_opacity'] != 100) {
        GV_Background_Opacity(map, opts['map_opacity']); // redundant if control has already been placed
    }
    if (opts['zoom_control'] && (opts['zoom_control'] == 'none' || opts['zoom_control'] === false)) {
        // no zoom control was requested
    } else {
        if (opts['zoom_control'] && opts['zoom_control'] == '3d') {
            map.addControl(new GLargeMapControl3D());
        } else if (opts['zoom_control'] && opts['zoom_control'] == 'small') {
            map.addControl(new GSmallMapControl());
        } else {
            map.addControl(new GLargeMapControl());
        }
    }
    if (opts['scale_control'] !== false) {
        map.addControl(new GScaleControl());
    }

    if (opts['tracklist_options'] && opts['tracklist_options']['tracklist']) {
        var id = (opts['tracklist_options']['id']) ? opts['tracklist_options']['id'] : 'gv_tracklist';
        GV_Place_Draggable_Box(map, id, opts['tracklist_options']['position'], opts['tracklist_options']['draggable'], opts['tracklist_options']['collapsible']);
    }

    if (self.gv_marker_list_options && gv_marker_list_exists) {
        var o = gv_marker_list_options;
        if (o['floating'] === false && o['id_static']) {
            o['id'] = o['id_static'];
        }
        else if (o['floating'] && o['id_floating']) {
            o['id'] = o['id_floating'];
        }
        else if (!o['id']) {
            o['id'] = 'gv_marker_list';
        }
        o['container_id'] = o['id'] + '_container';
        if (o['floating'] && o['container_id'] && $(o['container_id'])) {
            if (o['width']) {
                $(o['id']).style.width = o['width'] + 'px';
            }
            if (o['height']) {
                $(o['id']).style.height = o['height'] + 'px';
            }
            GV_Place_Draggable_Box(map, o['id'], o['position'], o['draggable'], o['collapsible']);
        } else {
            if ($(o['id'])) {
                $(o['id']).style.display = 'block';
            }
        }
        if (o['help_tooltips']) {
            gv_marker_list_text_tooltip = 'Click to ';
            gv_marker_list_icon_tooltip = 'Click to ';
            if (o['center']) {
                gv_marker_list_text_tooltip += 'center + ';
                gv_marker_list_icon_tooltip += 'center + ';
            }
            if (o['zoom']) {
                gv_marker_list_text_tooltip += 'zoom in + ';
                gv_marker_list_icon_tooltip += 'zoom in + ';
            }
            if (o['toggle']) {
                gv_marker_list_text_tooltip += 'hide/show the marker + ';
            }
            if (o['url_links'] && m.url) {
                gv_marker_list_text_tooltip += 'open the marker\'s link + ';
            }
            if (o['info_window'] && !o['toggle']) {
                gv_marker_list_text_tooltip += 'open the info window';
            }
            if (o['info_window']) {
                gv_marker_list_icon_tooltip += 'open the info window';
            }
            gv_marker_list_text_tooltip = gv_marker_list_text_tooltip.replace(/(Click to |, | \+ )$/, '');
            gv_marker_list_icon_tooltip = gv_marker_list_icon_tooltip.replace(/(Click to |, | \+ )$/, '');
        }
    }

    if (opts['legend_options'] && opts['legend_options']['legend']) {
        // the DIVs are as follows: container_id.table_id.(handle_id & id)
        var id = (opts['legend_options']['id']) ? opts['legend_options']['id'] : 'gv_legend';
        if ($(id) && $(id).innerHTML && !($(id).innerHTML.match(/^\s*(<!--[^>]*-->|)\s*$/))) {
            GV_Place_Draggable_Box(map, id, opts['legend_options']['position'], opts['legend_options']['draggable'], opts['legend_options']['collapsible']);
        }
    }

    if (opts['searchbox_options'] == true) {
        opts['searchbox_options'] = {searchbox: true};
    }
    if (opts['searchbox_options'] && opts['searchbox_options']['searchbox']) {
        var id = (opts['searchbox_options']['id']) ? opts['searchbox_options']['id'] : 'gv_searchbox';
        var pos = (opts['searchbox_options']['position'] && opts['searchbox_options']['position'].length >= 3) ? opts['searchbox_options']['position'] : ['G_ANCHOR_TOP_LEFT', 70, 6];
        GV_Place_Draggable_Box(map, id, pos, opts['searchbox_options']['draggable'], opts['searchbox_options']['collapsible']);
    }
    // allow the search "form" to be submitted via the return key:
    if ($('gv_searchbox_input') && $('gv_searchbox_button')) {
        $('gv_searchbox_input').onkeypress = function(e) {
            if (!e) {
                e = window.event;
            }
            if (e.keyCode == 13) {
                eval($('gv_searchbox_button').getAttributeNode('onclick').nodeValue);
                return false;
            }
        }
    }

    if (opts['center_coordinates'] !== false) {
        // set up and place the box that shows the center coordinates
        if (!$('gv_center_container')) {
            var center_div = document.createElement('div');
            center_div.id = 'gv_center_container';
            center_div.style.display = 'none';
            center_div.innerHTML = '<table style="cursor:crosshair; filter:alpha(opacity=80); -moz-opacity:0.80; opacity:0.80;" cellspacing="0" cellpadding="0" border="0"><tr valign="middle"><td><div id="gv_center_coordinates" class="gv_center_coordinates" onclick="GV_Toggle(\'gv_crosshair\'); gv_crosshair_temporarily_hidden = false;" title="Clicca qui per visualizzare o nascondere l\'indicatore di centro mappa"></div></td><td><div id="gv_measurement_icon" style="display:block; width:23px; height:15px; margin-left:3px; cursor:pointer;"><a href="javascript:void(0);" onclick="GV_Place_Measurement_Tools();" title="Clicca qui per strumenti di misurazione" style="cursor:pointer;"><img src="http://maps.gpsvisualizer.com/google_maps/ruler.gif" width="23" height="13" border="0" vspace="1"></a></div></td></tr></table>';
            map.getContainer().appendChild(center_div);
        }
        GV_Place_Control(map, 'gv_center_container', G_ANCHOR_BOTTOM_LEFT, 4, 40);

        // set up the box that holds the crosshair in the middle -- but use the GV_Setup_Crosshair function to center it
        if (!$('gv_crosshair_container')) {
            var crosshair_div = document.createElement('div');
            crosshair_div.id = 'gv_crosshair_container';
            crosshair_div.style.display = 'none';
            crosshair_div.className = 'gmnoprint';
            crosshair_div.innerHTML = '<div id="gv_crosshair" align="center" style="width:15px; height:15px; display:block;"><img src="http://maps.gpsvisualizer.com/google_maps/crosshair.gif" alt="" width="15" height="15" /></div>';
            // map.getPane(G_MAP_FLOAT_PANE).appendChild(crosshair_div);
            map.getContainer().appendChild(crosshair_div);
        }
        if ($('gv_crosshair')) {
            $('gv_crosshair').style.display = (opts['crosshair_hidden']) ? 'none' : 'block';
            gv_hidden_crosshair_is_still_hidden = true;
        }
        GV_Setup_Crosshair(map, {crosshair_container_id: 'gv_crosshair_container', crosshair_graphic_id: 'gv_crosshair', crosshair_width: 15, center_coordinates_id: 'gv_center_coordinates', fullscreen: opts['full_screen']});
    }

    if (opts['mouse_coordinates']) {
        // set up and place the box that shows the mouse coordinates
        if (!$('gv_mouse_container')) {
            var mouse_div = document.createElement('div');
            mouse_div.id = 'gv_mouse_container';
            mouse_div.style.display = 'none';
            mouse_div.innerHTML = '<table style="cursor:crosshair; filter:alpha(opacity=80); -moz-opacity:0.80; opacity:0.80;" cellspacing="0" cellpadding="0" border="0"><tr><td><div id="gv_mouse_coordinates" class="gv_mouse_coordinates">Mouse:&nbsp;</div></td></tr></table>';
            map.getContainer().appendChild(mouse_div);
        }
        GV_Place_Control(map, 'gv_mouse_container', G_ANCHOR_BOTTOM_LEFT, 4, 58);
        GEvent.addListener(map, "mousemove", function(mouse_coords) {
            if ($('gv_mouse_coordinates')) {
                $('gv_mouse_coordinates').innerHTML = 'Mouse: <span id="mouse_coordinate_pair">' + parseFloat(mouse_coords.lat().toFixed(5)) + ',' + parseFloat(mouse_coords.lng().toFixed(5)) + '</span>';
            }
        });
    }

    if (opts['measurement_tools'] == true) {
        opts['measurement_tools'] = {enabled: true};
    }
    if (opts['measurement_tools'] && opts['measurement_tools']['enabled']) {
        GV_Place_Measurement_Tools(opts['measurement_tools']);
    }

    if ($('gv_credit') && $('gv_credit').innerHTML.indexOf('gpsvisualizer.com') < 0) {
        var bogus_credit = $('gv_credit');
        bogus_credit.parentNode.removeChild(bogus_credit);
    }
    if (!$('gv_credit')) {
        var credit_div = document.createElement('div');
        credit_div.id = 'gv_credit';
        credit_div.style.display = 'none';
        credit_div.style.font = '10px Verdana,sans-serif';
        credit_div.style.padding = '1px';
        credit_div.style.backgroundColor = '#ffffff';
        credit_div.style.filter = 'alpha(opacity=80)';
        credit_div.style.MozOpacity = 0.80;
        credit_div.style.KhtmlOpacity = 0.80;
        credit_div.innerHTML = (gv_options['custom_credit'] && gv_options['custom_credit'].indexOf('gpsvisualizer.com') > -1) ? gv_options['custom_credit'] : '<b>Mappa creata con strumenti di <a target="_top" href="http://www.gpsvisualizer.com/">GPSVisualizer.com</a></b>';
        map.getContainer().appendChild(credit_div);
    }
    if (gv_options['credit_position'] && gv_options['credit_position'].length >= 3 && gv_options['credit_position'][1] < map.getContainer().clientWidth && gv_options['credit_position'][2] < map.getContainer().clientHeight) {
        GV_Place_Control(map, 'gv_credit', eval(gv_options['credit_position'][0]), gv_options['credit_position'][1], gv_options['credit_position'][2]);
    } else {
        GV_Place_Control(map, 'gv_credit', G_ANCHOR_BOTTOM_RIGHT, 6, 20);
    }

    gv_options['info_window_width_maximum'] = (map.getSize().width < 800) ? map.getSize().width - 100 : 700; // may as well do it here...

    // Why not just set them up now?
    eval(marker_array_name + ' = new Array();');
    eval(track_array_name + ' = new Array();');
    eval(track_info_array_name + ' = new Array();');
    eval(track_segments_array_name + ' = new Array();');
}

function GV_Place_Measurement_Tools(opts) {
    var o = (opts) ? opts : ((gv_options['measurement_tools']) ? gv_options['measurement_tools'] : []);
    if (o['distance'] !== false) {
        o['distance'] = true;
    }
    if (o['area'] !== false) {
        o['area'] = true;
    }
    if (!self.gmap || (!o['distance'] && !o['area'])) {
        return false;
    }
    if ($('gv_measurement_container')) {
        return false;
    } // don't duplicate it!
    var distance_color = (o['distance_color']) ? o['distance_color'] : '#0033ff';
    var area_color = (o['area_color']) ? o['area_color'] : '#ff00ff';
    var measurement_div = document.createElement('div');
    measurement_div.id = 'gv_measurement_tools';
    measurement_div.style.display = 'none';
    measurement_div.innerHTML = '<div id="gv_measurement_container" style="display:none;"><table id="gv_measurement_table" style="position:relative; filter:alpha(opacity=95); -moz-opacity:0.95; background:#ffffff;" cellpadding="0" cellspacing="0" border="0"><tr><td><div id="gv_measurement_handle" align="center" style="height:6px; max-height:6px; background:#CCCCCC; border-left:1px solid #999999; border-top:1px solid #EEEEEE; border-right:1px solid #999999; padding:0px; cursor:move;"><!-- --></div><div id="gv_measurement" align="left" style="font:11px Arial; line-height:13px; border:solid #000000 1px; background:#FFFFFF; padding:4px;"></div></td></tr></table></div>';
    gmap.getContainer().appendChild(measurement_div);
    var html = '<div style="max-width:220px;">';
    html += '<table cellspacing="0" cellpadding="0"><tr valign="top"><td>';
    html += '<table cellspacing="0" cellpadding="0">';
    if (o['distance']) {
        html += '<tr valign="top"><td style="padding-right:4px;"><img src="http://maps.google.com/mapfiles/kml/pal5/icon5.png" align="absmiddle" width="16" height="16"></td><td style="font-family:Arial; font-weight:bold;" nowrap>Misura una distanza</td></tr>';
        html += '<tr valign="top"><td></td><td><div id="gv_measurement_result_distance" style="font-family:Arial; font-weight:bold; font-size:12px; padding-bottom:4px; color:' + distance_color + ';"></div></td></tr>';
        html += '<tr valign="top"><td></td><td><div style="font-family:Arial;padding-bottom:12px;" id="gv_measurement_link_distance"></div></td></tr>';
    }
    if (o['area']) {
        html += '<tr valign="top"><td style="padding-right:4px;"><img src="http://sketchup.google.com/crimages/bm-154069-PolygonButton.png" align="absmiddle" width="16" height="16"></td><td style="font-family:Arial;font-weight:bold;" nowrap>Misura un\'area</td></tr>';
        html += '<tr valign="top"><td></td><td><div id="gv_measurement_result_area" style="font-family:Arial; font-weight:bold; font-size:12px; padding-bottom:4px; color:' + area_color + ';"></div></td></tr>';
        html += '<tr valign="top"><td></td><td><div style="font-family:Arial; padding-bottom:0px;" id="gv_measurement_link_area"></div></td></tr>';
    }
    html += '</table>';
    html += '</td><td align="right" style="width:12px; padding-left:8px;">';
    html += '<img src="http://maps.gstatic.com/mapfiles/iw_close.gif" width="12" height="12" border="0" style="cursor:pointer;" onclick="GV_Remove_Measurement_Tools(); $(\'gv_measurement_icon\').style.display = \'block\';" title="termina la misurazione e chiudi questo pannello" />';
    html += '</td></tr></table>';
    html += '</div>';
    $('gv_measurement').innerHTML = html;

    GV_Measurements.Init({
        distance: {link: 'gv_measurement_link_distance', result: 'gv_measurement_result_distance', color: distance_color, width: 2, opacity: 1.0}
        , area: {link: 'gv_measurement_link_area', result: 'gv_measurement_result_area', color: area_color, width: 2, opacity: 1.0, fill_opacity: 0.2}
    });

    var pos = (o['position'] && o['position'].length >= 3) ? o['position'] : ['G_ANCHOR_BOTTOM_LEFT', 4, 60];
    GV_Place_Draggable_Box(gmap, 'gv_measurement', pos, true, true);
    if ($('gv_measurement_container') && $('gv_measurement_icon')) {
        $('gv_measurement_icon').style.display = 'none';
    }
}
function GV_Remove_Measurement_Tools() {
    GV_Measurements.Cancel('area');
    GV_Measurements.Delete('area');
    GV_Measurements.Cancel('distance');
    GV_Measurements.Delete('distance');
    GV_Remove_Control('gv_measurement_container');
}

function GV_Place_Draggable_Box(map, id, position, draggable, collapsible) {
    // the DIVs are as follows: container_id.table_id.(handle_id & id)
    if (!id || !position) {
        return false;
    }
    var container_id = id + '_container';
    var table_id = id + '_table';
    var handle_id = id + '_handle';
    if ($(container_id) && position && position.length >= 3) {
        var vertical_offset = 2000;
        if ($(table_id) && $(handle_id) && draggable) {
            $(table_id).style.top = '-' + vertical_offset + 'px';
            if (position[0].indexOf('_BOTTOM_') > -1) {
                GV_Place_Control(map, container_id, eval(position[0]), position[1], position[2] - vertical_offset);
            } else {
                GV_Place_Control(map, container_id, eval(position[0]), position[1], position[2] + vertical_offset);
            }
            GV_Drag.init($(handle_id), $(table_id));
            if (collapsible !== false) {
                GV_Windowshade_Setup(handle_id, id);
            }
        } else {
            GV_Place_Control(map, container_id, eval(position[0]), position[1], position[2]);
            if ($(handle_id)) {
                $(handle_id).style.display = 'none';
            }
        }
    }
}

gv_waypoint_events_are_set_up = false;
function GV_Setup_Marker_Processing_Events(map_name, marker_array_name) {
    map_name = (map_name) ? map_name : 'gmap';
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    marker_array_name = (marker_array_name) ? marker_array_name : 'wpts';
    if (eval('self.' + marker_array_name)) {
        var marker_array = eval(marker_array_name);
        if (!gv_waypoint_events_are_set_up) {
            if (gv_filter_waypoints) {
                GEvent.addListener(map, "moveend", function() {
                    window.setTimeout('GV_Process_Markers(' + map_name + ',' + marker_array_name + ')', 50)
                });
                GEvent.addListener(map, "resize", function() {
                    window.setTimeout('GV_Process_Markers(' + map_name + ',' + marker_array_name + ')', 50)
                });
            }
            if (gv_options['dynamic_marker_options'] && gv_options['dynamic_marker_options']['url']) {
                GEvent.addListener(map, "moveend", function() {
                    window.setTimeout('GV_Get_Dynamic_Markers(gv_options)', 50)
                });
            }
            if (gv_options['dynamic_data'] && gv_options['dynamic_data'].length) {
                for (var i = 0; i < gv_options['dynamic_data'].length; i++) {
                    if (gv_options['dynamic_data'][i] && gv_options['dynamic_data'][i]['url'] && gv_options['dynamic_data'][i]['reload_on_move']) {
                        gv_options['dynamic_data'][i]['autozoom'] = false; // important! otherwise it goes into a zoom/move infinite loop
                        if (gv_options['zoom'] && gv_options['zoom'] == 'auto') {
                            gv_options['zoom'] = 8;
                        } // important! otherwise it goes into a zoom/move infinite loop
                        var func = 'GV_Reload_Markers_From_File(' + i + ')';
                        GEvent.addListener(map, "moveend", function() {
                            window.setTimeout(func, 50)
                        });
                    }
                }
            }
            gv_waypoint_events_are_set_up = true;
        }
        GV_Process_Markers(map, marker_array);
    }
}
function GV_Setup_Waypoint_Processing_Events(map_name, marker_array_name) {
    GV_Setup_Marker_Processing_Events(map_name, marker_array_name);
}


function GV_Process_Markers(map, marker_array) {
    if (!marker_array) {
        return false;
    }

    var gv_infowindow_open = false;
    if (map.getInfoWindow() && map.getInfoWindow().isHidden() === false) {
        gv_infowindow_open = true;
    }
    if (gv_filter_waypoints) {
        GV_Filter_Markers_In_View(map, marker_array);
    }
    if (gv_marker_list_exists) {
        GV_Marker_List();
    }
    // If labels are supposed to be visible, we need to re-do them after filtering
    if (self.gv_labels_are_visible && gv_labels_are_visible && self.gv_options && gv_options['marker_array']) {
        GV_Toggle_All_Labels(gv_options['marker_array'], true);
    }
    if (gv_filter_waypoints && self.gv_marker_filter_moved_enough && gv_marker_filter_moved_enough === true && gv_infowindow_open && typeof(self.gv_open_infowindow_index) != 'undefined' && gv_open_infowindow_index != null) {
        GEvent.trigger(marker_array[gv_open_infowindow_index], "click");
        GEvent.trigger(marker_array[gv_open_infowindow_index], "mouseout");
    }
}
function GV_Process_Waypoints(map, marker_array) {
    GV_Process_Markers(map, marker_array);
}

gv_dynamic_file_index = -1;
gv_reloading_data = false;
gv_static_marker_count = -1; // global variables that need to be set just once

function GV_Finish_Map(opts) {
    if (!opts && !self.gv_options) {
        return false;
    } else if (!opts && self.gv_options) {
        opts = gv_options;
    }
    var map_name = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    var marker_array_name = (opts['marker_array']) ? opts['marker_array'] : 'wpts';
    var marker_array = eval('self.' + marker_array_name);
    if (!marker_array) {
        eval(marker_array_name + ' = []');
    }
    var track_array_name = (opts['track_array']) ? opts['track_array'] : 'trk';
    var track_info_array_name = (opts['track_info_array']) ? opts['track_info_array'] : 'trk_info';
    var track_segments_array_name = (opts['track_segments_array']) ? opts['track_segments_array'] : 'trk_segments';
    var track_array = eval('self.' + track_array_name);

    // Make a copy of the "static" waypoints already in the map so they can be referred to later
    if (self.gv_static_marker_count < 0) {
        gv_static_marker_count = (marker_array) ? marker_array.length : 0;
    }

    // The Google Spreadsheets and XML functions call GV_Finish_Map on their own
    if (gv_options['dynamic_data'] && !gv_options['dynamic_data'].length) { // make it into an array
        gv_options['dynamic_data'] = [gv_options['dynamic_data']];
    }

    if (gv_options['dynamic_data'] && gv_options['dynamic_data'][gv_dynamic_file_index + 1] && (gv_options['dynamic_data'][gv_dynamic_file_index + 1]['url'] || gv_options['dynamic_data'][gv_dynamic_file_index + 1]['google_spreadsheet']) && !gv_options['dynamic_data'][gv_dynamic_file_index + 1]['processed'] && gv_options['dynamic_data'][gv_dynamic_file_index + 1]['load_on_open'] !== false) {
        gv_dynamic_file_index += 1;
        GV_Load_Markers_From_File(); // this will call GV_Finish_Map
        return true;
    }
    if (opts['zoom'] == 'auto') { // || (!opts['zoom'] && opts['zoom'] != '0')) {
        GV_Autozoom({map: map_name, adjustment: gv_options['autozoom_adjustment'], 'default': gv_options['autozoom_default']}, eval('self.' + track_array_name), eval('self.' + marker_array_name));
        opts['center'] = [map.getCenter().lat(), map.getCenter().lng()];
        map.setMapType(eval(opts['map_type']));
    }
    gv_dynamic_data_last_center = map.getCenter();
    gv_dynamic_data_last_zoom = map.getZoom(); // for reload-on-move purposes

    if (eval('self.' + track_info_array_name) && opts['filter_tracks']) {
        // The delay on tracks is slightly longer than that of waypoints because we want track filtering to happen AFTER waypoint filtering, in case some waypoints are attached to tracks
        GEvent.addListener(map, "moveend", function() {
            window.setTimeout('GV_Filter_Tracks(' + map_name + ',' + track_info_array_name + ')', 75);
        });
    }

    if (opts['full_screen']) {
        window.onresize = function() {
            GV_Fill_Window_With_Map(map.getContainer().id);
            map.checkResize();
        }
        window.setTimeout(map_name + '.checkResize(); if(!gv_reloading_data){' + map_name + '.setCenter(new GLatLng(' + opts['center'][0] + ',' + opts['center'][1] + '));}', 100); // give the full-screen map a moment to think before filling the screen and recentering
    }

    if (window.location.toString().match(/[&\\?\#](gv_force_[a-z]+)=([^&]+)/)) {
        window.setTimeout("GV_Recenter_Per_URL({center_key:'gv_force_recenter',zoom_key:'gv_force_zoom',maptype_key:'gv_force_maptype'})", 100);
    }
    if (gv_options['centering_options']) {
        window.setTimeout("GV_Recenter_Per_URL(gv_options['centering_options'])", 100);
    }

    if (!track_array || track_array.length == 0) { // if there are no tracks, the tracklist should be removed, unless it's been customized/repurposed
        if (gv_options['tracklist_options'] && gv_options['tracklist_options']['tracklist'] && $('gv_tracklist_links') && !$('gv_tracklist_links').innerHTML.match(/\S/)) {
            GV_Remove_Control('gv_tracklist_container');
        }
    }

    // Now that everything's settled down, that hidden crosshair can finally be shown (when the map is first moved)
    window.setTimeout("gv_hidden_crosshair_is_still_hidden = false;", 150);

    // Move the processing of the waypoints to the very end so the centering and resizing don't trigger extra filtering jobs
    window.setTimeout('GV_Setup_Marker_Processing_Events("' + map_name + '","' + marker_array_name + '")', 200); // the delay lets IE6 realize the markers are in the cache so it doesn't reload them all if there's a marker list
}

function GV_Autozoom() { // automatically adjust the map's zoom level to cover the elements passed to this function
    // EXAMPLE 1:  GV_Autozoom({map:'gmap',adjustment:-1},trk,wpts);
    // EXAMPLE 2:  GV_Autozoom({map:'gmap',adjustment:-2},wpts[2]);
    // EXAMPLE 3:  GV_Autozoom({map:'gmap',adjustment:1},trk[1],wpts[0],wpts[4]);
    var opts = arguments[0];
    arguments.shift;
    opts['map'] = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    opts['adjustment'] = (opts['adjustment'] && parseInt(opts['adjustment'])) ? parseInt(opts['adjustment']) : 0;
    opts['default'] = (opts['default'] && parseInt(opts['default'])) ? parseInt(opts['default']) : 8;
    opts['margin'] = (opts['margin'] && parseInt(opts['margin'])) ? parseInt(opts['margin']) : 0; // margin actually doesn't affect anything; bug on Google's end?
    opts['save_position'] = (opts['save_position'] === false || opts['save_position'] == 0) ? false : true;

    var map = eval(opts['map']);
    if (!map) {
        return false;
    }
    var min_lat = 90;
    var max_lat = -90;
    var min_lon = 360;
    var max_lon = -360;
    for (var i = 0; i < arguments.length; i++) {
        if (arguments[i]) {
            if (arguments[i].getLatLng) { // it's a waypoint
                if (arguments[i].getLatLng().lng() < min_lon) {
                    min_lon = arguments[i].getLatLng().lng();
                }
                if (arguments[i].getLatLng().lng() > max_lon) {
                    max_lon = arguments[i].getLatLng().lng();
                }
                if (arguments[i].getLatLng().lat() < min_lat) {
                    min_lat = arguments[i].getLatLng().lat();
                }
                if (arguments[i].getLatLng().lat() > max_lat) {
                    max_lat = arguments[i].getLatLng().lat();
                }
            } else if (typeof(arguments[i]) == 'object' && arguments[i].length) { // it's an array of waypoints or tracks, or just a track
                if (arguments[i][0] && arguments[i][0].getVertexCount) { // it's a single track
                    for (ts = 0; ts < arguments[i].length; ts++) { // tracks are collections of track segments
                        var bounds = arguments[i][ts].getBounds();
                        if (bounds) {
                            var w = bounds.getSouthWest().lng();
                            var s = bounds.getSouthWest().lat();
                            var e = bounds.getNorthEast().lng();
                            var n = bounds.getNorthEast().lat();
                            if (w < min_lon) {
                                min_lon = w;
                            }
                            if (e > max_lon) {
                                max_lon = e;
                            }
                            if (s < min_lat) {
                                min_lat = s;
                            }
                            if (n > max_lat) {
                                max_lat = n;
                            }
                        }
                    }
                } else {
                    for (var j = 0; j < arguments[i].length; j++) { // it's a collection of tracks or waypoints
                        if (arguments[i][j]) {
                            if (arguments[i][j][0] && arguments[i][j][0].getVertexCount) { // it's a track
                                for (ts = 0; ts < arguments[i][j].length; ts++) { // tracks are collections of track segments (but also sometimes waypoints)
                                    if (!arguments[i][j][ts].coords) { // if it DID have "coords," it'd be an associated waypoint, NOT a track segment!
                                        var bounds = arguments[i][j][ts].getBounds();
                                        if (bounds) {
                                            var w = bounds.getSouthWest().lng();
                                            var s = bounds.getSouthWest().lat();
                                            var e = bounds.getNorthEast().lng();
                                            var n = bounds.getNorthEast().lat();
                                            if (w < min_lon) {
                                                min_lon = w;
                                            }
                                            if (e > max_lon) {
                                                max_lon = e;
                                            }
                                            if (s < min_lat) {
                                                min_lat = s;
                                            }
                                            if (n > max_lat) {
                                                max_lat = n;
                                            }
                                        }
                                    }
                                }
                            } else if (arguments[i][j].getLatLng) { // it's a waypoint
                                if (arguments[i][j].getLatLng().lng() < min_lon) {
                                    min_lon = arguments[i][j].getLatLng().lng();
                                }
                                if (arguments[i][j].getLatLng().lng() > max_lon) {
                                    max_lon = arguments[i][j].getLatLng().lng();
                                }
                                if (arguments[i][j].getLatLng().lat() < min_lat) {
                                    min_lat = arguments[i][j].getLatLng().lat();
                                }
                                if (arguments[i][j].getLatLng().lat() > max_lat) {
                                    max_lat = arguments[i][j].getLatLng().lat();
                                }
                            }
                        }
                    }
                }
            } // end if (arguments[i].getLatLng)
        } // end if (arguments[i]) 
    }
    if (min_lat != 90 && max_lat != -90 && min_lon != 360 && max_lon != -360) {
        var bounds = new GLatLngBounds(new GLatLng(min_lat, min_lon), new GLatLng(max_lat, max_lon));
        var zoom = map.getBoundsZoomLevel(bounds, new GSize(map.getSize().width - (opts['margin'] * 2), map.getSize().height - (opts['margin'] * 2)));
        if (min_lat == max_lat && min_lon == max_lon) {
            zoom = (opts['default']);
        } // arbitrary zoom for single-point maps
        // var new_bounds_rectangle = new GPolygon([new GLatLng(max_lat,min_lon),new GLatLng(max_lat,max_lon),new GLatLng(min_lat,max_lon),new GLatLng(min_lat,min_lon),new GLatLng(max_lat,min_lon)],'#0000ff',1,0.3,'#0000ff',0.2);
        // map.addOverlay(new_bounds_rectangle);
        map.setCenter(bounds.getCenter(), zoom + opts['adjustment']);
        if (opts['save_position']) {
            map.savePosition();
        }
    }
}


var gv_marker_count = 0;
if (!self.gv_options) {
    gv_options = new Array();
    gv_options['map'] = null;
} // turns out ".map" is a predefined method!

gv_options['info_window_width'] = parseFloat(gv_options['info_window_width']) || 0;
gv_options['thumbnail_width'] = parseFloat(gv_options['thumbnail_width']) || 0;
gv_options['photo_width'] = parseFloat(gv_options['photo_width']) || 0;
gv_options['photo_size'] = (gv_options['photo_size'] && gv_options['photo_size'].length == 2 && gv_options['photo_size'][0] > 0) ? gv_options['photo_size'] : null;

function GV_Draw_Marker(info) {
    if (self.gmap && self.wpts) {
        wpts.push(GV_Marker(gmap, info));
    }
}

gv_synthesize_fields_pattern = new RegExp('\{([^\{]*)\}', 'gi');

function GV_Marker(map, marker_info, lon, name, desc, url, color, style, width, label_id) {
    // The following variables need to have been defined:
    //   gv_icons (array of icon info), gv_options['icon_directory'] (absolute URL)
    //   gv_options['default_marker']['icon'] (string), gv_options['default_marker']['color'] (string), gv_options['marker_link_target'] (string)
    //   google_api_version (number), gv_marker_list_exists (boolean), gv_marker_list_html (string)

    var m = {};

    // The old "GV_Marker" function had everything in a particular order; this new one uses more user-friendly named parameters.
    // If the second argument has a 'lat' item INSIDE of it, then it's the new version; otherwise that's just the latitude.
    if (marker_info['lat'] != undefined || marker_info['address'] != undefined) {
        m = marker_info;
        if (m['style'] && !m['icon']) {
            m['icon'] = m['style'];
        } // this one changed recently
    } else {
        m['lat'] = marker_info;
        m['lon'] = lon;
        m['name'] = name;
        m['desc'] = desc;
        m['url'] = url;
        m['color'] = color;
        m['icon'] = style;
        m['window_width'] = width;
        m['label_id'] = label_id;
    }

    if (m['address'] && !m['lat']) { // allow an "address" field to define the location in a pinch
        var geocoder = new GClientGeocoder();
        geocoder.getLatLng(
                m['address'],
                function(coords) {
                    if (coords) {
                        m['address'] = null; // to prevent infinite loops
                        m['lat'] = coords.lat();
                        m['lon'] = coords.lng();
                        GV_Marker(map, m);
                    }
                }
        );
        return;
    }

    if (gv_options['synthesize_fields']) {
        for (var f in gv_options['synthesize_fields']) {
            if (gv_options['synthesize_fields'][f]) {
                var template = gv_options['synthesize_fields'][f];
                template = template.replace(gv_synthesize_fields_pattern,
                        function(complete_match, field_name) {
                            if (m[field_name] || m[field_name] == '0' || m[field_name] === false) {
                                return (m[field_name]);
                            } else {
                                return ('');
                            }
                        }
                );
                m[f] = (template.match(/^\s*$/)) ? '' : template;
            }
        }
    }

    var tempIcon = new GIcon(gv_default_icon_object);
    var custom_scale = (m['scale'] > 0 && (gv_options['default_marker']['scale'] != m['scale'])) ? true : false;
    var scale = (m['scale'] > 0) ? m['scale'] : (gv_options['default_marker']['scale']) ? gv_options['default_marker']['scale'] : 1;
    if (parseFloat(m['opacity']) == 100) {
        m['opacity'] = 1;
    }
    var opacity = (m['opacity'] > 0) ? parseFloat(m['opacity']) : (gv_options['default_marker']['opacity']) ? gv_options['default_marker']['opacity'] : 1;
    if (m['icon'] == 'tickmark') {
        opacity = 1;
    }

    if (m['icon_url'] || (m['icon'] && m['icon'].toString().indexOf('/') > -1) || (gv_garmin_icons && gv_garmin_icons[m['icon']])) {
        if (gv_garmin_icons && gv_garmin_icons[m['icon']] && gv_garmin_icons[m['icon']]['url']) {
            tempIcon.image = gv_garmin_icons[m['icon']]['url'];
            tempIcon.iconSize = new GSize(16 * scale, 16 * scale);
            var anchor_x = 7;
            var anchor_y = 7;
            if (gv_garmin_icons[m['icon']]['anchor'] && gv_garmin_icons[m['icon']]['anchor'].length == 2) {
                anchor_x = gv_garmin_icons[m['icon']]['anchor'][0];
                anchor_y = gv_garmin_icons[m['icon']]['anchor'][1];
            }
            if (gv_options['garmin_icon_set'] == '24x24') {
                tempIcon.iconSize = new GSize(24 * scale, 24 * scale);
                anchor_x *= 1.5;
                anchor_y *= 1.5;
            }
            tempIcon.iconAnchor = new GPoint(anchor_x * scale, anchor_y * scale);
        } else {
            tempIcon.image = (m['icon_url']) ? m['icon_url'] : m['icon'] || gv_options['default_marker']['icon'];
            tempIcon.image = tempIcon.image.replace(/^c:\//, 'file:///c:/'); // fix local Windows file names
            var default_width = 32;
            var default_height = 32;
            if (tempIcon.image.match(/chart\.apis\.google\.com\/.*\bch\w+=[\w_]*pin\b/)) {
                default_width = 21;
                default_height = 34;
            } // is there a more efficient way to do this??
            default_width = (self.gv_marker_icon_size && gv_marker_icon_size[0]) ? gv_marker_icon_size[0] : default_width;
            default_height = (self.gv_marker_icon_size && gv_marker_icon_size[1]) ? gv_marker_icon_size[1] : default_height;
            tempIcon.iconSize = (m['icon_size'] && m['icon_size'][0] && m['icon_size'][1]) ? new GSize(m['icon_size'][0] * scale, m['icon_size'][1] * scale) : new GSize(default_width * scale, default_height * scale);
            var default_anchor_x = tempIcon.iconSize.width * 0.5;
            var default_anchor_y = tempIcon.iconSize.height * 0.5;
            if (tempIcon.image.match(/chart\.apis\.google\.com\/.*\bch\w+=[\w_]*pin\b/)) {
                default_anchor_x = 10;
                default_anchor_y = 33;
            } // is there a more efficient way to do this??
            default_anchor_x = (self.gv_marker_icon_anchor && gv_marker_icon_anchor[0] != null) ? gv_marker_icon_anchor[0] : default_anchor_x;
            default_anchor_y = (self.gv_marker_icon_anchor && gv_marker_icon_anchor[1] != null) ? gv_marker_icon_anchor[1] : default_anchor_y;
            tempIcon.iconAnchor = (m['icon_anchor'] && m['icon_anchor'][0] != null && m['icon_anchor'][1] != null) ? new GPoint(m['icon_anchor'][0] * scale, m['icon_anchor'][1] * scale) : new GPoint(default_anchor_x, default_anchor_y);
        }
        tempIcon.infoWindowAnchor = new GPoint(tempIcon.iconSize.width * 0.75, 0);
        tempIcon.shadow = null;
        tempIcon.shadowSize = null;
        tempIcon.infoShadowAnchor = null;
        tempIcon.transparent = null;
        tempIcon.imageMap = null;
    } else if (m['icon'] || m['color'] || m['letter'] || m['rotation'] != undefined || opacity != 1 || m['icon_anchor'] || custom_scale) {
        var icon = (m['icon'] && gv_icons[m['icon']]) ? m['icon'] : gv_options['default_marker']['icon'];
        var color = (m['color']) ? m['color'].toLowerCase() : gv_options['default_marker']['color'].toLowerCase();
        if (color.substring(0, 1) == '#') {
            color = color.replace(/^\#/, '');
        }
        var base_url = (gv_icons[icon]['directory']) ? gv_icons[icon]['directory'] : gv_options['icon_directory'] + icon;
        tempIcon.iconAnchor = (m['icon_anchor'] && m['icon_anchor'][0] != null && m['icon_anchor'][1] != null) ? new GPoint(m['icon_anchor'][0] * scale, m['icon_anchor'][1] * scale) : new GPoint(gv_icons[icon]['ia'][0] * scale, gv_icons[icon]['ia'][1] * scale);
        if (icon != gv_options['default_marker']['icon'] || custom_scale) { // these only need to be messed with if they're not using the default icon or if a scale has been specified; otherwise, they were set as part of "gv_default_icon_object"
            tempIcon.iconSize = new GSize(gv_icons[icon]['is'][0] * scale, gv_icons[icon]['is'][1] * scale);
            tempIcon.infoWindowAnchor = (gv_icons[icon]['iwa'] && gv_icons[icon]['iwa'][0]) ? new GPoint(gv_icons[icon]['iwa'][0] * scale, gv_icons[icon]['iwa'][1] * scale) : new GPoint(tempIcon.iconSize.width * 0.75, 0);
            if (gv_options['marker_shadows'] === false || m['no_shadow']) {
                tempIcon.shadow = null;
                tempIcon.shadowSize = null;
                tempIcon.infoShadowAnchor = null;
            } else {
                tempIcon.shadow = (gv_icons[icon]['ss'] && gv_icons[icon]['ss'][0]) ? base_url + '/shadow.png' : null;
                tempIcon.shadowSize = (gv_icons[icon]['ss'] && gv_icons[icon]['ss'][0]) ? new GSize(gv_icons[icon]['ss'][0] * scale, gv_icons[icon]['ss'][1] * scale) : null;
                tempIcon.infoShadowAnchor = (gv_icons[icon]['isa'] && gv_icons[icon]['isa'][0]) ? new GPoint(gv_icons[icon]['isa'][0] * scale, gv_icons[icon]['isa'][1] * scale) : new GPoint(tempIcon.iconSize.width * 0.5, tempIcon.iconSize.height * 1);
            }
            if (custom_scale) {
                tempIcon.imageMap = [];
                for (var j = 0; j < gv_icons[icon]['im'].length; j++) {
                    tempIcon.imageMap[j] = gv_icons[icon]['im'][j] * scale;
                }
            }
            else {
                tempIcon.imageMap = gv_icons[icon]['im'];
            }
        }
        if (icon.indexOf('/') < 0) { // it's a custom icon, but still a GPSV standard icon, so opacity and rotation will be handled via the URL of the image
            // OPACITY:
            var op = '';
            if (opacity != 1) {
                op = (opacity < 1) ? '-' + Math.round(opacity * 100) : '-' + Math.round(opacity);
                tempIcon.shadow = null;
                tempIcon.shadowSize = null;
            }
            // ROTATION:
            var rotation = (typeof(m['rotation']) != 'undefined') ? '-r' + (1000 + (5 * Math.round(((parseFloat(m['rotation']) + 360) % 360) / 5))).toString().substring(1, 4) : '';
            tempIcon.image = base_url + '/' + color.toLowerCase() + rotation + op + '.png';
            // LETTERS:
            if (m['letter'] && gv_icons[icon]['letters']) {
                tempIcon.transparent = base_url + '/transparent-' + m['letter'].toUpperCase() + '.png'; // not the most kosher solution, but it seems to work
            } else {
                tempIcon.transparent = base_url + '/' + color.toLowerCase() + rotation + '-t.png';
            }
        }
    }

    // overload!!!!
    if (window.location.toString().indexOf('moveon.org') > -1) {
        var color = (m['color']) ? m['color'].toLowerCase() : gv_options['default_marker']['color'].toLowerCase();
        tempIcon.image = 'http://labs.google.com/ridefinder/images/mm_20_' + color + '.png';
        tempIcon.shadow = 'http://labs.google.com/ridefinder/images/mm_20_shadow.png';
        tempIcon.transparent = null;
    }

    var marker = (google_api_version >= 2) ? new GMarker(new GLatLng(m['lat'], m['lon']), {icon: tempIcon, draggable: false}) : new GMarker(new GPoint(m['lon'], m['lat']), tempIcon);
    gv_marker_count += 1;
    var target = (m['link_target']) ? m['link_target'] : (gv_options['marker_link_target']) ? gv_options['marker_link_target'] : '_blank';
    var target_attribute = 'target="' + target + '"';
    if (self.gv_marker_list_options && gv_marker_list_options['folder_field'] && m[gv_marker_list_options['folder_field']]) {
        m['folder'] = m[gv_marker_list_options['folder_field']];
    }
    if (m['no_window']) {
        if (m['url']) {
            GEvent.addListener(marker, "click", function() {
                window.open(m['url'], target);
            });
        }
    } else {
        var iw_html = '';
        var url_quoted = (m['url']) ? m['url'].replace(/"/g, "&quot;") : '';
        if (m['name']) {
            if (m['url'] && m['url'] != null) {
                iw_html = iw_html + '<div class="gv_marker_info_window_name"><b><a ' + target_attribute + ' href="' + url_quoted + '" title="' + url_quoted + '">' + m['name'] + '</a></b></div>';
            }
            else {
                iw_html = iw_html + '<div class="gv_marker_info_window_name">' + m['name'] + '</div>';
            }
        }
        if (m['thumbnail'] && !m['photo']) {
            var tn_style = (m['thumbnail_width']) ? ' style="width:' + parseFloat(m['thumbnail_width']) + 'px;"' : (gv_options['thumbnail_width'] > 0) ? ' style="width:' + gv_options['thumbnail_width'] + 'px;"' : '';
            var thumbnail = '<img class="gv_marker_thumbnail" src="' + m['thumbnail'] + '"' + tn_style + '>';
            if (m['url']) {
                thumbnail = '<a ' + target_attribute + ' href="' + url_quoted + '">' + thumbnail + '</A>';
            }
            iw_html = iw_html + thumbnail;
        } else if (m['photo']) {
            var photo_style = '';
            if (m['photo_size']) {
                if (m['photo_size'].length == 2) {
                    photo_style = ' style="width:' + parseFloat(m['photo_size'][0]) + 'px; height:' + parseFloat(m['photo_size'][1]) + 'px;"';
                } else if (m['photo_size'].match(/([0-9]+)[^0-9]+([0-9]+)/)) {
                    var parts = m['photo_size'].match(/([0-9]+)[^0-9]+([0-9]+)/);
                    photo_style = ' style="width:' + parseFloat(parts[1]) + 'px; height:' + parseFloat(parts[2]) + 'px;"';
                } else if (parseFloat(m['photo_size']) > 0) {
                    photo_style = ' style="width:' + parseFloat(m['photo_size']) + 'px;"';
                }
            } else if (gv_options['photo_size']) { // if this isn't null, it's ALWAYS a 2-element array
                photo_style = ' style="width:' + parseFloat(gv_options['photo_size'][0]) + 'px; height:' + parseFloat(gv_options['photo_size'][1]) + 'px;"';
            }
            if (photo_style == '') {
                photo_style = (m['photo_width']) ? ' style="width:' + parseFloat(m['photo_width']) + 'px;"' : (gv_options['photo_width'] > 0) ? ' style="width:' + gv_options['photo_width'] + 'px;"' : '';
            }
            iw_html = iw_html + '<div class="gv_marker_info_window_photo"><img class="gv_marker_photo" src="' + m['photo'] + '"' + photo_style + '></div>';
        }
        if (m['desc'] && m['desc'] != '-') {
            iw_html = iw_html + '<div class="gv_marker_info_window_desc">' + m['desc'] + '</div>';
        }
        if (m['dd'] || (gv_options['driving_directions'] && m['dd'] !== false)) {
            var dd_name = (m['name']) ? ' (' + m['name'].replace(/<[^>]*>/g, '').replace(/\(/g, '[').replace(/\)/g, ']').replace(/"/g, "&quot;") + ')' : '';
            var saddr = (gv_options['driving_directions_start']) ? gv_options['driving_directions_start'].replace(/"/g, "&quot;") : '';
            iw_html = iw_html + '<table class="gv_driving_directions" cellspacing="0" cellpadding="0" border="0"><tr><td><form action="http://maps.google.com/maps" target="_blank" style="margin:0px;">';
            iw_html = iw_html + '<input type="hidden" name="daddr" value="' + (m['dd_lat'] || m['lat']) + ',' + (m['dd_lon'] || m['lon']) + dd_name + '">';
            iw_html = iw_html + '<p class="gv_driving_directions_heading" style="margin:2px 0px 4px 0px; white-space:nowrap">Driving directions to this point</p>';
            iw_html = iw_html + '<p style="margin:0px; white-space:nowrap;">Enter your starting address:<br><input type="text" size="20" name="saddr" value="' + saddr + '">&nbsp;<input type="submit" value="Go"></p>';
            iw_html = iw_html + '</td></tr></table>';
        }
        var window_width = 0;
        var marker_window_width = parseFloat(m['window_width']);
        if (marker_window_width > 0 && marker_window_width < gv_options['info_window_width_maximum']) {
            window_width = marker_window_width;
        }
        else if (gv_options['info_window_width'] > 0 && gv_options['info_window_width'] < gv_options['info_window_width_maximum']) {
            window_width = gv_options['info_window_width'];
        }
        if (window_width > 0 && window_width < 200) {
            window_width = 200;
        } // apparently you can't make it less than 217 (let's leave 17 for the close box though)
        var width_style = (window_width > 0) ? 'width:' + window_width + 'px;' : '';
        var info_html = '<div style="text-align:left; ' + width_style + '" class="gv_marker_info_window">' + iw_html + '</div>';
        if (iw_html) {
            GEvent.addListener(marker, "click", function() {
                marker.openInfoWindowHtml(info_html, {maxWidth: gv_options['info_window_width_maximum']});
                gv_open_infowindow_index = marker.index;
            });
            if (m['draw_track']) {
                GEvent.addListener(marker, "click", function() {
                    if (window.addedOverlays) {
                        var ovl = window.addedOverlays;
                        for (var j = 0; j < ovl.length; j++)
                            map.removeOverlay(ovl[j]);
                    }

                    var request = new XMLHttpRequest();
                    request.open("GET", "RouteScript.axd?ScriptForRoute=" + uri_escape(m['route_name']), true);
                    request.setRequestHeader("Content-Type", "application/x-javascript;");
                    request.onreadystatechange = function() {
                        if (request.readyState == 4 && request.status == 200) {
                            if (request.responseText) {
                                try {
                                    eval(request.responseText);
                                    addTracks();
                                }
                                catch (e) {
                                    alert(e);
                                }
                            }
                        }
                    };
                    request.send(null);
                });
            }
        }

    }

    var out_of_range = false;
    if (gv_filter_waypoints) {
        var min_lat = map.getBounds().getSouthWest().lat();
        var min_lon = map.getBounds().getSouthWest().lng();
        var max_lat = map.getBounds().getNorthEast().lat();
        var max_lon = map.getBounds().getNorthEast().lng();
        if (max_lon < min_lon) {
            min_lon = -180;
            max_lon = 180;
        } // Date Line weirdness
        if (m['lon'] < min_lon || m['lon'] > max_lon || m['lat'] < min_lat || m['lat'] > max_lat) {
            out_of_range = true;
        }
    }

    if (m['label'] || m['label_id']) { // draw a permanent label
        var label_text = (m['label']) ? m['label'] : m['name'];
        if (label_text != '') {
            var label_id = (m['label_id']) ? m['label_id'] : gv_marker_array_name + '_label[' + (gv_marker_count - 1) + ']';
            var label_class = (m['label_class']) ? 'gv_label ' + m['label_class'] : 'gv_label';
            var label_style = (m['label_color']) ? 'background:#ffffff; border-color:' + m['label_color'] + '; color:' + m['label_color'] + ';' : '';
            var label_hide = (gv_options['hide_labels']) ? true : false;
            var offset_x = gv_options['label_offset'][0];
            var offset_y = gv_options['label_offset'][1];
            var label_centered = gv_options['label_centered'];
            var label_left = gv_options['label_left'];
            if (m['label_offset'] && m['label_offset'].length > 1) {
                offset_x = m['label_offset'][0];
                offset_y = m['label_offset'][1];
            }
            if ((m['label_centered'] == true && !label_centered) || (m['label_centered'] === false && label_centered)) {
                label_centered = m['label_centered'];
            }
            if ((m['label_left'] == true && !label_left) || (m['label_left'] === false && label_left)) {
                label_left = m['label_left'];
                label_centered = false;
            }
            var label = new ELabel(new GLatLng(m['lat'], m['lon']), label_text, label_class, new GSize(parseInt(0.75 + tempIcon.iconSize.width / 2), tempIcon.iconSize.height - tempIcon.iconAnchor.y), new GSize(offset_x, offset_y), 100, true, label_id, label_hide, label_centered, label_left, label_style);
            if (!gv_filter_waypoints) { // if filtering is on, the GV_Process_Markers & GV_Filter_Markers_In_View functions will add them to the map
                map.addOverlay(label);
            }
            marker.label_object = label;
        }
    }
    if (m['name'] || m['thumbnail']) {
        if (google_api_version >= 2) {
            // v2 tooltips, adapted from http://www.econym.demon.co.uk/googlemaps/tooltips4.htm
            if (!$('gv_marker_tooltip')) {
                gv_marker_tooltip = GV_Initialize_Marker_Tooltip(map);
            } // initialize it if it hasn't been done yet
            var tooltip_html = m['name'] + ' ';
            if (m['thumbnail']) {
                var tn_style = (m['thumbnail_width']) ? ' style="width:' + parseFloat(m['thumbnail_width']) + 'px;"' : (gv_options['thumbnail_width'] > 0) ? ' style="width:' + gv_options['thumbnail_width'] + 'px;"' : '';
                tooltip_html = tooltip_html + '<img class="gv_marker_thumbnail" src="' + m['thumbnail'] + '"' + tn_style + '>';
            }
            if (m['photo']) {
                tooltip_html = tooltip_html + '<img class="gv_marker_photo" src="' + m['photo'] + '">';
            } // photo is hidden in tooltip but gets pre-loaded!
            marker.tooltip = '<div class="gv_tooltip">' + tooltip_html + '</div>';
            GEvent.addListener(marker, 'mouseover', function() {
                GV_Create_Marker_Tooltip(map, marker);
            });
            GEvent.addListener(marker, 'mouseout', function() {
                GV_Hide_Marker_Tooltip();
            });
        } else {
            // v1 tooltips, adapted from http://www.econym.demon.co.uk/googlemaps1/tooltips.htm
            var topElement = marker.images[0];
            if (marker.iconImage) {
                topElement = marker.iconImage;
            }
            if (marker.transparentIcon) {
                topElement = marker.transparentIcon;
            }
            if (marker.imageMap) {
                topElement = marker.imageMap;
            }
            topElement.setAttribute("title", m['name']);
        }
    }
    if (google_api_version >= 2) {
        // This info can be used by other functions, like the "marker list":
        marker.index = gv_marker_count - 1;
        marker.name = (m['name']) ? m['name'] : gv_name_of_unnamed_marker;
        marker.desc = (m['desc']) ? m['desc'] : '';
        marker.url = (m['url']) ? m['url'] : '';
        marker.shortdesc = (m['shortdesc']) ? m['shortdesc'] : '';
        marker.html = info_html;
        marker.window_width = (m['window_width']) ? m['window_width'] : '';
        marker.color = (m['color']) ? m['color'].toLowerCase() : gv_options['default_marker']['color'].toLowerCase();
        marker.icon = (m['icon']) ? m['icon'] : gv_options['default_marker']['icon'];
        marker.width = tempIcon.iconSize.width;
        marker.height = tempIcon.iconSize.height;
        marker.image = tempIcon.image;
        marker.coords = new GLatLng(m['lat'], m['lon']);
        marker.thumbnail = (m['thumbnail']) ? m['thumbnail'] : '';
        marker.thumbnail_width = (m['thumbnail_width']) ? m['thumbnail_width'] : '';
        marker.type = (m['type']) ? m['type'] : '';
        marker.zoom_level = (m['zoom_level']) ? m['zoom_level'] : '';
        marker.folder = (m['folder']) ? m['folder'] : '';
        marker.dynamic = (m['dynamic']) ? m['dynamic'] : false;

        if (gv_marker_list_exists && (m['type'] != 'tickmark' || gv_marker_list_options['include_tickmarks']) && (m['type'] != 'trackpoint' || gv_marker_list_options['include_trackpoints']) && !m['nolist']) {
            marker.list_html = GV_Marker_List_Item(marker, gv_marker_list_map_name, gv_marker_array_name + '[' + (gv_marker_count - 1) + ']');
            if (!gv_filter_marker_list || !out_of_range) {
                if (gv_marker_list_options['limit'] && gv_marker_list_options['limit'] > 0 && gv_marker_list_count >= gv_marker_list_options['limit']) {
                    // do nothing; we're over the limit
                } else {
                    if (typeof(marker.list_html) != 'undefined') {
                        if (m['folder']) {
                            gv_marker_list_folders[m['folder']] = (gv_marker_list_folders[m['folder']]) ? gv_marker_list_folders[m['folder']] + marker.list_html : marker.list_html;
                        } else {
                            gv_marker_list_html += marker.list_html;
                        }
                        gv_marker_list_count += 1;
                    }
                }
            }
        }
    }
    if (!gv_filter_waypoints) { // if filtering is on, the GV_Process_Markers & GV_Filter_Markers_In_View functions will add them to the map
        map.addOverlay(marker);
    }

    if (m['track_number'] && self.trk && self.trk[m['track_number']]) {
        trk[m['track_number']].push(marker);
        if (self.trk_info && self.trk_info[m['track_number']] && trk_info[m['track_number']]['hidden']) {
            map.removeOverlay(marker);
            marker.gv_hidden = true;
        }
    }

    return marker;
}

function GV_Initialize_Marker_Tooltip(map) {
    var mtt = document.createElement('div');
    mtt.id = 'gv_marker_tooltip';
    mtt.style.visibility = 'hidden';
    map.getPane(G_MAP_FLOAT_PANE).appendChild(mtt);
    return (mtt);
}
function GV_Create_Marker_Tooltip(map, marker) {
    // copied almost verbatim from http://www.econym.demon.co.uk/googlemaps/tooltips4.htm
    if (!marker || !marker.tooltip) {
        return false;
    }
    gv_marker_tooltip.innerHTML = marker.tooltip;
    var point = map.getCurrentMapType().getProjection().fromLatLngToPixel(map.fromDivPixelToLatLng(new GPoint(0, 0), true), map.getZoom());
    var offset = map.getCurrentMapType().getProjection().fromLatLngToPixel(marker.getPoint(), map.getZoom());
    var anchor = marker.getIcon().iconAnchor;
    var width = marker.getIcon().iconSize.width;
    var height = gv_marker_tooltip.clientHeight;
    offset.x += -1;
    offset.y += 4; // a little adjustment
    height = 18; // makes all tooltips hover near the icon, even if they're tall and have thumbnails or whatnot (they expand downward instead of upward)
    var pos = new GControlPosition(G_ANCHOR_TOP_LEFT, new GSize(offset.x - point.x - anchor.x + width, offset.y - point.y - anchor.y - height * 0.75));
    pos.apply(gv_marker_tooltip);
    gv_marker_tooltip.style.visibility = 'visible';
}
function GV_Hide_Marker_Tooltip() {
    gv_marker_tooltip.style.visibility = 'hidden';
}

function GV_Initialize_Track_Tooltip(map) {
    var ttt = document.createElement('div');
    ttt.id = 'gv_track_tooltip';
    ttt.style.visibility = 'hidden';
    map.getPane(G_MAP_FLOAT_PANE).appendChild(ttt);
    return (ttt);
}
function GV_Create_Track_Tooltip(map, info) {
    // copied almost verbatim from http://www.econym.demon.co.uk/googlemaps/tooltips4.htm
    if (!self.gv_track_tooltip || !info || (!info.bounds && !info.center) || !info.name) {
        return false;
    }
    gv_track_tooltip.innerHTML = '<div class="gv_tooltip gv_track_tooltip"><span style="color:' + info.color + ';">' + info.name + '</span></div>';
    var lat = (info.center && info.center.lat) ? info.center.lat : (info.bounds.n + info.bounds.s) / 2;
    var lon = (info.center && info.center.lon) ? info.center.lon : (info.bounds.e + info.bounds.w) / 2;
    var point = map.getCurrentMapType().getProjection().fromLatLngToPixel(map.fromDivPixelToLatLng(new GPoint(0, 0), true), map.getZoom());
    var offset = map.getCurrentMapType().getProjection().fromLatLngToPixel(new GLatLng(lat, lon), map.getZoom());
    var width = gv_track_tooltip.clientWidth;
    var height = gv_track_tooltip.clientHeight;
    var pos = new GControlPosition(G_ANCHOR_TOP_LEFT, new GSize(offset.x - point.x - width / 2, offset.y - point.y - height / 2));
    pos.apply(gv_track_tooltip);
    gv_track_tooltip.style.visibility = 'visible';
}
function GV_Hide_Track_Tooltip() {
    if (self.gv_track_tooltip) {
        gv_track_tooltip.style.visibility = 'hidden';
    }
}


function GV_Marker_List() {
    if (gv_marker_list_exists) {
        var header = (gv_marker_list_options['header']) ? gv_marker_list_options['header'] : '';
        var footer = (gv_marker_list_options['footer']) ? gv_marker_list_options['footer'] : '';
        var top_border = (gv_marker_list_options['dividers']) ? '<div class="gv_marker_list_border_top"></div>' : '';
        var folders = '';
        gv_marker_list_folder_state = [];
        gv_marker_list_folder_name = [];
        if (self.gv_marker_list_folders) {
            var folder_count = 0;
            var minus_graphic = 'http://maps.gpsvisualizer.com/google_maps/minus.gif';
            var plus_graphic = 'http://maps.gpsvisualizer.com/google_maps/plus.gif';
            var folder_triangle_open = 'http://maps.gpsvisualizer.com/google_maps/folder_triangle_open.gif';
            var folder_triangle_closed = 'http://maps.gpsvisualizer.com/google_maps/folder_triangle_closed.gif';
            var open_graphic = folder_triangle_open;
            var closed_graphic = folder_triangle_closed;
            var toggle_message = "show/hide this folder's markers";
            var collapse_message = "open/close this folder";
            // These next variables are defaults for when the folders are pre-collapsed when the map first loads
            var all_collapsed = (gv_marker_list_options['folders_collapsed']) ? true : false;
            for (var folder_name in gv_marker_list_folders) {
                folder_count += 1;
                var c = (all_collapsed) ? true : false;
                var h = false;
                gv_marker_list_folder_state[folder_count] = {collapsed: c, hidden: h};
                gv_marker_list_folder_name[folder_count] = folder_name;
                var initial_icon = (c) ? closed_graphic : open_graphic;
                var initial_contents_display = (c) ? 'none' : 'block';
                var collapse_onclick = "GV_Folder_Collapse_Toggle(" + folder_count + ");";
                var toggle_onclick = "GV_Folder_Visibility_Toggle(" + folder_count + ");";
                var folder_name_onclick = toggle_onclick;
                var folder_name_message = toggle_message;
                if (typeof(gv_marker_list_options['folder_name_click']) != 'undefined') {
                    if (gv_marker_list_options['folder_name_click'].match(/coll/i)) {
                        folder_name_onclick = collapse_onclick;
                        folder_name_message = toggle_message;
                    }
                    else if (gv_marker_list_options['folder_name_click'].match(/toggle|vis|viz/i)) {
                        folder_name_onclick = toggle_onclick;
                        folder_name_message = toggle_message;
                    }
                    else if (gv_marker_list_options['folder_name_click'] === false || gv_marker_list_options['folder_name_click'].match(/no/i)) {
                        folder_name_onclick = '';
                        folder_name_message = '';
                    }
                }
                var toggle_checkbox_checked = 'checked';
                var this_folder = '<div class="gv_marker_list_folder" id="folder_' + folder_count + '">';
                this_folder += '<div class="gv_marker_list_folder_header" id="folder_header_' + folder_count + '"><table cellspacing="0" cellpadding="0" border="0" width="100%"><tr valign="top">';
                this_folder += '<td align="left" nowrap><img src="' + initial_icon + '" width="11" height="11" hspace="2" vspace="0" id="folder_collapse_' + folder_count + '" style="cursor:pointer" title="' + collapse_message + '" onclick="' + collapse_onclick + '"></td>';
                this_folder += '<td align="left"><input type="checkbox" id="folder_checkbox_' + folder_count + '" class="gv_marker_list_folder_checkbox" style="width:12px; height:12px; padding:0px; margin:0px 2px 0px 0px;" ' + toggle_checkbox_checked + ' title="' + toggle_message + '" onclick="' + toggle_onclick + '"></td>';
                this_folder += '<td width="99%" align="left"><div class="gv_marker_list_folder_name" id="folder_name_' + folder_count + '" title="' + folder_name_message + '" onclick="' + folder_name_onclick + '" style="cursor:pointer; max-width:100%;">' + folder_name + '</div></td>'; // this has to be a DIV with a width or max-width, otherwise IE won't adjust its opacity
                this_folder += '</tr></table></div>';
                this_folder += '<div class="gv_marker_list_folder_contents" id="folder_contents_' + folder_count + '" style="display:' + initial_contents_display + '; max-width:100%;">' + gv_marker_list_folders[folder_name] + '</div>';
                this_folder += '</div>';
                folders = folders + this_folder;
            }
        }
        top_border = (!gv_marker_list_html) ? '' : top_border;
        gv_marker_list_div.innerHTML = header + top_border + folders + gv_marker_list_html + footer;
        gv_marker_list_count = 0;
        if (gv_options['marker_list_options'] && gv_options['marker_list_options']['collapsed_folders'] && gv_options['marker_list_options']['collapsed_folders'].length) {
            for (var i = 0; i < gv_options['marker_list_options']['collapsed_folders'].length; i++) {
                GV_Folder_Collapse_Toggle(gv_options['marker_list_options']['collapsed_folders'][i], false);
            }
        }
        if (gv_options['marker_list_options'] && gv_options['marker_list_options']['hidden_folders'] && gv_options['marker_list_options']['hidden_folders'].length) {
            for (var i = 0; i < gv_options['marker_list_options']['hidden_folders'].length; i++) {
                GV_Folder_Visibility_Toggle(gv_options['marker_list_options']['hidden_folders'][i], false);
            }
        }
    }
    if ($(gv_marker_list_div_name + '_handle') && $(gv_marker_list_div_name + '_table')) {
        if (!$(gv_marker_list_div_name + '_handle').root) { // only needs to be done once
            GV_Drag.init($(gv_marker_list_div_name + '_handle'), $(gv_marker_list_div_name + '_table'));
        }
    }
}

function GV_Marker_List_Item(m, map_name, marker_name) {
    var default_color = (gv_marker_list_options['default_color']) ? gv_marker_list_options['default_color'] : '';
    var color = (gv_marker_list_options['colors']) ? m.color : default_color;
    var color_style = (color) ? 'color:' + color : '';

    var unhide = '';
    var center = (gv_marker_list_options['center']) ? map_name + '.setCenter(' + marker_name + '.coords); ' : '';
    var zoom_in = '';
    if (gv_marker_list_options['zoom'] && (gv_marker_list_options['zoom_level'] || m.zoom_level)) {
        var zoom_level = (m.zoom_level) ? parseFloat(m.zoom_level) : parseFloat(gv_marker_list_options['zoom_level']);
        zoom_in = map_name + '.setZoom(' + zoom_level + '); ';
    }
    else if (gv_marker_list_options['zoom']) {
        zoom_in = map_name + '.zoomIn(); ';
    }
    var hide_crosshair = (gv_marker_list_options['center'] && $('gv_crosshair')) ? "$('gv_crosshair').style.display = 'none'; gv_crosshair_temporarily_hidden = true; " : '';
    var text_toggle = (gv_marker_list_options['toggle']) ? 'GV_Toggle_Marker(' + map_name + ',' + marker_name + ',this,\'' + color + '\');' : ''; // only affects text
    var text_open_info_window = (gv_marker_list_options['info_window'] && !gv_marker_list_options['toggle']) ? 'GEvent.trigger(' + marker_name + ',\'click\'); ' : ''; // disable info windows upon text clicking if "toggle" is activated
    var icon_open_info_window = (gv_marker_list_options['info_window']) ? 'GEvent.trigger(' + marker_name + ',\'click\'); ' : '';

    if (gv_filter_waypoints && (center || zoom_in)) {
        text_open_info_window = "window.setTimeout('" + text_open_info_window.replace(/'/g, "\\'") + "',100); ";
        icon_open_info_window = "window.setTimeout('" + icon_open_info_window.replace(/'/g, "\\'") + "',100); ";
    }
    var mouseover = (m.tooltip) ? 'onmouseover="GV_Create_Marker_Tooltip(' + map_name + ',' + marker_name + ');" ' : '';
    var mouseout = (m.tooltip) ? 'onmouseout="gv_marker_tooltip.style.visibility = \'hidden\';" ' : '';

    var text_click = unhide + text_toggle + zoom_in + center + hide_crosshair + text_open_info_window;
    var icon_click = unhide + zoom_in + center + hide_crosshair + icon_open_info_window;

    var thumbnail = '';
    if (m.thumbnail) {
        var tn_display = (gv_marker_list_options['thumbnails']) ? 'display:block; ' : '';
        var tn_width = (m.thumbnail_width) ? 'width:' + m.thumbnail_width + 'px; ' : '';
        thumbnail = '<div class="gv_marker_list_thumbnail" style="' + tn_display + '"><img class="gv_marker_thumbnail" src="' + m.thumbnail + '" style="' + tn_width + tn_display + '"></div>';
    }

    var css_wrap_style = '';
    var table_wrap_style = '';
    var border_class_top = '';
    var border_class_bottom = '';
    var icon_margin_right = 4;
    if (!gv_marker_list_options['wrap_names']) {
        css_wrap_style = 'white-space:nowrap; ';
        table_wrap_style = 'nowrap';
    }
    if (1 == 2 && gv_marker_list_options['wrap_names'] && gv_marker_list_options['icons']) {
        indent_style = 'margin-left:' + (m.width + icon_margin_right) + 'px; text-indent:-' + (m.width + icon_margin_right) + 'px ';
    } // 1==2 because we don't need this when we're doing "float:left" on the icons
    var icon_scaling = 'width:' + m.width + 'px; height:' + m.height + 'px';
    var icon = (gv_marker_list_options['icons']) ? '<img title="' + gv_marker_list_icon_tooltip + '" class="gv_marker_list_item_icon" ' + mouseover + mouseout + 'onClick="' + icon_click + '" style="' + icon_scaling + '" src="' + m.image + '" alt="">' : '';
    var target = (gv_options['marker_link_target']) ? 'target="' + gv_options['marker_link_target'] + '"' : '';
    var name = '<div title="' + gv_marker_list_text_tooltip + '" ' + mouseover + mouseout + 'onClick="' + text_click + '" class="gv_marker_list_item_name" style="' + color_style + ';">' + m.name + thumbnail + '</div>';
    name = (gv_marker_list_options['url_links'] && m.url) ? '<a ' + target + ' href="' + m.url + '" title="' + m.url + '">' + name + '</a>' : name;
    var d = (m.shortdesc) ? m.shortdesc : m.desc;
    var desc = (gv_marker_list_options['desc'] && d && d != '-') ? '<div class="gv_marker_list_item_desc" style="white-space:normal; ' + color_style + '">' + d + '</div>' : '';
    // NOTE: the only reason 'gv_marker_list_first_item' and 'gv_marker_list_item_bottom' still exist is for backwards compatibility with the old style-based way of adding borders.
    var first_class = (gv_marker_count != 1) ? '' : ' gv_marker_list_first_item';
    var bottom_border_class = (gv_marker_list_options['dividers']) ? 'gv_marker_list_border_bottom' : '';
    var html = '<div id="gv_list:' + marker_name + '" class="gv_marker_list_item' + first_class + '"><table cellspacing="0" cellpadding="0" border="0"><tr valign="top" align="left"><td>' + icon + '</td><td style="' + css_wrap_style + '">' + name + desc + '</td></tr></table></div><div class="gv_marker_list_item_bottom ' + bottom_border_class + '" style="clear:both;"></div>' + "\n";
    return (html);
}

function GV_Toggle_Marker(map, marker, link, link_color, dimmed_color) {
    if (marker.gv_hidden) {
        map.addOverlay(marker);
        marker.gv_hidden = false;
    } else {
        map.removeOverlay(marker);
        marker.gv_hidden = true;
    }
    if (link_color && link.style.color) {
        link_color = GV_Color_Hex2CSS(link_color);
        dimmed_color = (dimmed_color) ? GV_Color_Hex2CSS(dimmed_color) : GV_Color_Hex2CSS('#999999');
        if (marker.gv_hidden) {
            link.style.color = dimmed_color;
        }
        else {
            link.style.color = link_color;
        }
    }
}

function GV_Toggle_All_Labels(marker_array_name, force_show) {
    if (!marker_array_name) {
        marker_array_name = 'wpts';
    }
    var visible = null;
    if (force_show || gv_options['hide_labels']) {
        visible = true;
        gv_labels_are_visible = true;
        gv_options['hide_labels'] = false;
    } else {
        visible = false;
        gv_labels_are_visible = false;
        gv_options['hide_labels'] = true;
    }
    if (eval('self.' + marker_array_name)) {
        var marker_array = eval(marker_array_name);
        if (marker_array && marker_array.length > 0) {
            for (var i = 0; i < marker_array.length; i++) {
                GV_Label_Visibility(marker_array_name, i, visible);
            }
        }
    }
}

function GV_Label_Visibility(marker_array_name, marker_index, visible) {
    var label_id = marker_array_name + '_label[' + marker_index + ']';
    var visibility = (visible) ? 'visible' : 'hidden';
    if ($(label_id)) {
        // Note that it's the parentNode -- the div containing the label -- that gets hidden or shown
        $(label_id).parentNode.style.visibility = visibility;
    }
}


function GV_Draw_Track(t) {
    window.addedOverlays = [];
    var map_name = (gv_options['map'] && typeof(gv_options['map']) !== 'function') ? gv_options['map'] : 'gmap';
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    if (!trk_segments[t] || !trk_info[t]) {
        return false;
    }
    trk[t] = [];
    var trk_color = (trk_info[t]['color']) ? GV_Color_Name2Hex(trk_info[t]['color']) : '#ff0000';
    var trk_fill_color = (trk_info[t]['fill_color']) ? GV_Color_Name2Hex(trk_info[t]['fill_color']) : trk_color;
    var trk_opacity = (trk_info[t]['opacity']) ? parseFloat(trk_info[t]['opacity']) : 1;
    var trk_fill_opacity = (trk_info[t]['fill_opacity']) ? parseFloat(trk_info[t]['fill_opacity']) : 0;
    var trk_width = (trk_info[t]['width']) ? parseFloat(trk_info[t]['width']) : 3;
    if (trk_width <= 0.1) {
        trk_width = 0;
    }
    var trk_outline_color = (trk_info[t]['outline_color']) ? GV_Color_Name2Hex(trk_info[t]['outline_color']) : '#000000';
    var trk_outline_opacity = (trk_info[t]['outline_opacity']) ? parseFloat(trk_info[t]['outline_opacity']) : 1;
    var trk_outline_width = (trk_info[t]['outline_width']) ? parseFloat(trk_info[t]['outline_width']) : 0;
    var bounds = new GLatLngBounds();
    var lat_sum = 0;
    var lon_sum = 0;
    var point_count = 0;
    var segment_points = new Array();
    for (s = 0; s < trk_segments[t].length; s++) {
        if (trk_segments[t][s]['points'].length > 0) {
            segment_points[s] = new Array();
            for (p = 0; p < trk_segments[t][s]['points'].length; p++) {
                var pt = new GLatLng(trk_segments[t][s]['points'][p][0], trk_segments[t][s]['points'][p][1]);
                segment_points[s].push(pt);
                bounds.extend(pt);
                lat_sum += trk_segments[t][s]['points'][p][0];
                lon_sum += trk_segments[t][s]['points'][p][1];
                point_count += 1;
            }
        }
        if (trk_outline_width > 0) { // while we're here, may as well save having to do another loop, since the outline doesn't have per-segment settings anyway:
            trk[t].push(new GPolyline(segment_points[s], trk_outline_color, trk_outline_width, trk_outline_opacity, {mouseOutTolerance: 1}));
            var ovl = trk[t][trk[t].length - 1];
            window.addedOverlays.push(ovl);
            map.addOverlay(ovl);
        }
    }
    for (s = 0; s < trk_segments[t].length; s++) {
        if (segment_points[s].length > 0) {
            var segment_color = (trk_segments[t][s]['color']) ? GV_Color_Name2Hex(trk_segments[t][s]['color']) : trk_color;
            var segment_opacity = (trk_segments[t][s]['opacity']) ? parseFloat(trk_segments[t][s]['opacity']) : trk_opacity;
            var segment_width = (trk_segments[t][s]['width']) ? parseFloat(trk_segments[t][s]['width']) : trk_width;
            var segment_outline_width = (trk_segments[t][s]['outline_width']) ? parseFloat(trk_segments[t][s]['outline_width']) : trk_outline_width;
            if (trk_fill_opacity > 0) { // segments can't have their own fill opacity (yet)
                trk[t].push(new GPolygon(segment_points[s], segment_color, segment_width, segment_opacity, trk_fill_color, trk_fill_opacity, {mouseOutTolerance: 1}));
            } else {
                trk[t].push(new GPolyline(segment_points[s], segment_color, segment_width, segment_opacity, {mouseOutTolerance: 1}));
            }
            var ovl = trk[t][trk[t].length - 1];
            window.addedOverlays.push(ovl);
            map.addOverlay(ovl);
        }
    }
    trk_segments[t] = []; // bad idea?
    if (!trk_info[t]['bounds']) {
        trk_info[t]['bounds'] = {w: bounds.getSouthWest().lng(), s: bounds.getSouthWest().lat(), e: bounds.getNorthEast().lng(), n: bounds.getNorthEast().lat()};
    }
    if (!trk_info[t]['center'] && point_count > 0) {
        trk_info[t]['center'] = {lon: (lon_sum / point_count), lat: (lat_sum / point_count)};
    }
    if (!$('gv_track_tooltip')) {
        gv_track_tooltip = GV_Initialize_Track_Tooltip(gmap);
    } // initialize it if it hasn't been done yet
    if (trk_info[t]['clickable'] !== false && (trk_info[t]['name'] || trk_info[t]['desc'])) {
        GV_Make_Track_Clickable(t);
    }
    if (trk_info[t]['hidden']) {
        GV_Toggle_Overlays(map, trk[t], false);
        trk[t].gv_hidden = true;
    }
}

function GV_Finish_Track(trk_index) {
    if (!self.trk || !self.trk_info || !self.gmap) {
        return false;
    }
    if (!$('gv_track_tooltip')) {
        gv_track_tooltip = GV_Initialize_Track_Tooltip(gmap);
    } // initialize it if it hasn't been done yet
    if (trk_info[trk_index] && trk_info[trk_index]['clickable'] !== false && (trk_info[trk_index]['name'] || trk_info[trk_index]['desc'])) {
        GV_Make_Track_Clickable(trk_index);
    }
}

function GV_Make_Track_Clickable(trk_index) {
    for (var i = 0; i < trk[trk_index].length; i++) {
        trk[trk_index][i].clickable = true;
        var html = '<div class="gv_marker_info_window"><div class="gv_marker_info_window_name">' + trk_info[trk_index]['name'] + '</div><div class="gv_marker_info_window_desc">' + trk_info[trk_index]['desc'] + '</div>';
        trk[trk_index][i].click_listener = GEvent.addListener(trk[trk_index][i], "click", function(coords) {
            gmap.openInfoWindowHtml(coords, html);
        });
        // GEvent.addListener(trk[trk_index][i], "mouseover", function(){ GV_Create_Track_Tooltip(gmap,trk_info[trk_index]); });
        // GEvent.addListener(trk[trk_index][i], "mouseout", function(){ GV_Hide_Track_Tooltip(); });
        trk[trk_index][i].mouseover_listener = GEvent.addListener(trk[trk_index][i], "mouseover", function() {
        }); // change the cursor but don't DO anything
    }

}

function GV_Enable_Area_Calculation() {
    GEvent.addListener(gmap, "click", function(overlay, latlng, overlaylatlng) {
        if (overlay && overlaylatlng && overlay.getVertexCount) {
            GV_Area_Calculation_Info_Window(overlay, overlaylatlng);
        }
    });
}
function GV_Area_Calculation_Info_Window(overlay, coords) {
    gv_current_overlay = overlay;
    var html = '';
    var area_sqm = 0;
    if (overlay.getArea) {
        area_sqm = overlay.getArea();
    } else {
        var v = [];
        for (i = 0; i < overlay.getVertexCount(); i++) {
            v.push(overlay.getVertex(i));
        }
        var temporary_polygon = new GPolygon(v, '#ffffff', 1, 0.1, '#ffffff', 0.05);
        gmap.addOverlay(temporary_polygon);
        area_sqm = temporary_polygon.getArea();
        gmap.removeOverlay(temporary_polygon);
    }
    var area_sqkm = area_sqm / 1000000;
    var area_sqmi = area_sqm / 2589988.11;
    var area_ha = area_sqm / 10000;
    var area_ac = area_sqm / 4046.85642;
    html += '<div style="font:11px Verdana; margin-bottom:4px; width:280px;">This shape\'s approximate area:</div>';
    html += '<table cellspacing=0 cellpadding=0 border=0><tr valign="top"><td style="font:11px Verdana; padding-right:12px;">';
    html += area_sqkm.toFixed(2) + ' km<sup style="font-size:70%">2</sup><br>';
    html += area_ha.toFixed(2) + ' ha<br>';
    html += '</td><td style="font:11px Verdana">';
    html += area_sqmi.toFixed(2) + ' mi.<sup style="font-size:70%">2</sup><br>';
    html += area_ac.toFixed(2) + ' acres';
    html += '</td></tr></table>';
    if (gv_current_overlay.gv_editing) {
        html += '<div style="font:11px Verdana; margin-top:8px"><a href="javascript:void(0)" onclick="gv_current_overlay.disableEditing(); gv_current_overlay.gv_editing = false; this.innerHTML=\'\';">finish editing</a>';
    } else {
        html += '<div style="font:11px Verdana; margin-top:8px"><a href="javascript:void(0)" onclick="gv_current_overlay.enableEditing(); gmap.closeInfoWindow(); gv_current_overlay.gv_editing = true; GEvent.addListener(gmap,\'singlerightclick\',function(point,src,overlay){ if(overlay && typeof(overlay.index)!==\'undefined\'){ gv_current_overlay.deleteVertex(overlay.index); gv_current_overlay.enableDrawing(); } }); \">edit this shape</a>';
    }
    gmap.openInfoWindowHtml(coords, html);
}

GV_Measurements = new function() {
    this.Overlay = [];
    this.Attributes = [];
    this.LinkBox = [];
    this.ResultBox = [];
    this.Listener = [];
    this.Length = [];
    this.Area = [];
    this.Text = {
        distance: {
            New: '<a href="javascript:void(0);" onclick="GV_Measurements.New(\'distance\');">Disegna una linea</a>',
            Cancel: 'Clicca sulla mappa per aggiungere punti, clicca col tasto destro per cancellarli, oppure <a href="javascript:void(0);" onclick="GV_Measurements.Cancel(\'distance\');"><nobr>clicca qui</nobr></a> per terminare',
            Edit: '<a href="javascript:void(0);" onclick="GV_Measurements.Edit(\'distance\');">Modifica</a> la linea, oppure <a href="javascript:void(0);" onclick="GV_Measurements.Delete(\'distance\');">cancellala</a>'
        },
        area: {
            New: '<a href="javascript:void(0);" onclick="GV_Measurements.New(\'area\');">Disegna una forma</a>',
            Cancel: 'Clicca sulla mappa per aggiungere punti, clicca col destro per cancellarli, oppure <a href="javascript:void(0);" onclick="GV_Measurements.Cancel(\'area\');"><nobr>clicca qui</nobr></a> per terminare',
            Edit: '<a href="javascript:void(0);" onclick="GV_Measurements.Edit(\'area\');">Modifica</a> la forma, oppure <a href="javascript:void(0);" onclick="GV_Measurements.Delete(\'area\');">cancellala</a>'
        }
    };
    this.Init = function(info) {
        if ($(info['distance']['link'  ])) {
            this.LinkBox['distance'] = $(info['distance']['link'  ]);
            this.LinkBox['distance'].innerHTML = this.Text['distance']['New'];
        }
        if ($(info['distance']['result'])) {
            this.ResultBox['distance'] = $(info['distance']['result']);
        }
        if ($(info['area'    ]['link'  ])) {
            this.LinkBox['area'    ] = $(info['area'    ]['link'  ]);
            this.LinkBox['area'    ].innerHTML = this.Text['area'    ]['New'];
        }
        if ($(info['area'    ]['result'])) {
            this.ResultBox['area'    ] = $(info['area'    ]['result']);
        }
        this.Attributes['distance'] = [];
        this.Attributes['distance']['color'] = (info['distance'] && info['distance']['color']) ? info['distance']['color'] : '#0033ff';
        this.Attributes['distance']['width'] = (info['distance'] && info['distance']['width']) ? info['distance']['width'] : 3;
        this.Attributes['distance']['opacity'] = (info['distance'] && info['distance']['opacity']) ? info['distance']['opacity'] : 1.0;
        this.Attributes['area'] = [];
        this.Attributes['area']['color'] = (info['area'] && info['area']['color']) ? info['area']['color'] : '#ff00ff';
        this.Attributes['area']['width'] = (info['area'] && info['area']['width']) ? info['area']['width'] : 2;
        this.Attributes['area']['opacity'] = (info['area'] && info['area']['opacity']) ? info['area']['opacity'] : 1.0;
        this.Attributes['area']['fill_opacity'] = (info['area'] && info['area']['fill_opacity']) ? info['area']['fill_opacity'] : 0.2;
    }
    this.New = function(key) {
        if (key == 'area') {
            this.Overlay[key] = new GPolygon([], this.Attributes[key]['color'], this.Attributes[key]['width'], this.Attributes[key]['opacity'], this.Attributes[key]['color'], this.Attributes[key]['fill_opacity'], {mouseOutTolerance: 2});
        } else {
            this.Overlay[key] = new GPolyline([], this.Attributes[key]['color'], this.Attributes[key]['width'], this.Attributes[key]['opacity'], {mouseOutTolerance: 2});
        }
        gmap.addOverlay(this.Overlay[key]);
        this.Edit(key);
    }
    this.Edit = function(key) {
        other_key = (key == 'area') ? 'distance' : 'area';
        if (this.Overlay[other_key]) {
            this.Cancel(other_key);
        }
        this.Overlay[key].enableDrawing();
        if (this.Listener['lineupdated']) {
            GEvent.removeListener(this.Listener['lineupdated']);
        }
        if (this.Listener['singlerightclick']) {
            GEvent.removeListener(this.Listener['singlerightclick']);
        }
        if (this.Listener['endline']) {
            GEvent.removeListener(this.Listener['endline']);
        }
        this.Listener['lineupdated'] = eval("GEvent.addListener(this.Overlay['" + key + "'],'lineupdated',function(){ GV_Measurements.Overlay['" + key + "'].disableEditing(); GV_Measurements.Overlay['" + key + "'].enableDrawing(); GV_Measurements.Calculate('" + key + "'); });");
        this.Listener['singlerightclick'] = eval("GEvent.addListener(gmap,'singlerightclick',function(point,src,overlay){ if(overlay && typeof(overlay.index)!=='undefined'){ GV_Measurements.Overlay['" + key + "'].deleteVertex(overlay.index); GV_Measurements.Overlay['" + key + "'].enableDrawing(); } });");
        this.Listener['endline'] = eval("GEvent.addListener(this.Overlay['" + key + "'],'endline',function(){ GV_Measurements.LinkBox['" + key + "'].innerHTML = GV_Measurements.Text['" + key + "']['Edit']; if (GV_Measurements.Listener['lineupdated']) { GEvent.removeListener(GV_Measurements.Listener['lineupdated']); } });");
        if (this.LinkBox[key]) {
            this.LinkBox[key].innerHTML = this.Text[key]['Cancel'];
        }
    }
    this.Cancel = function(key) {
        if (this.Overlay[key] && this.Overlay[key].getVertexCount() == 0) {
            this.Delete(key);
            return false;
        }
        if (this.Overlay[key]) {
            this.Overlay[key].disableEditing();
        }
        if (this.LinkBox[key]) {
            this.LinkBox[key].innerHTML = this.Text[key]['Edit'];
        }
    }
    this.Delete = function(key) {
        if (this.Overlay[key]) {
            this.Overlay[key].disableEditing();
            gmap.removeOverlay(this.Overlay[key]);
        }
        this.Overlay[key] = null;
        if (this.LinkBox[key]) {
            this.LinkBox[key].innerHTML = this.Text[key]['New'];
        }
        if (this.ResultBox[key]) {
            this.ResultBox[key].innerHTML = '';
        }
    }
    this.Calculate = function(key) {
        var result = '';
        if (this.Overlay[key] && this.Overlay[key].getVertexCount() > 1) {
            if (key == 'distance') {
                var meters = 0;
                for (i = 1; i < this.Overlay[key].getVertexCount(); i++) {
                    meters += this.Overlay[key].getVertex(i - 1).distanceFrom(this.Overlay[key].getVertex(i));
                }
                if (meters < 304.8) {
                    result += meters.toFixed(0) + ' m&nbsp; (' + (meters * 3.28084).toFixed(0) + ' ft.)';
                } else if (meters >= 304.8 && meters < 1000) {
                    result += meters.toFixed(0) + ' m&nbsp; (' + (meters / 1609.344).toFixed(2) + ' mi.)';
                } else if (meters >= 1000 && meters < 10000) {
                    result += (meters / 1000).toFixed(2) + ' km&nbsp; (' + (meters / 1609.344).toFixed(2) + ' mi.)';
                } else if (meters >= 10000 && meters < 1000000) {
                    result += (meters / 1000).toFixed(1) + ' km&nbsp; (' + (meters / 1609.344).toFixed(1) + ' mi.)';
                } else if (meters >= 1000000) {
                    result += (meters / 1000).toFixed(0) + ' km&nbsp; (' + (meters / 1609.344).toFixed(0) + ' mi.)';
                }
                result += '<br><span style="font-size:10px; color:#666666;">(da Google, &#177;0.3%)</span>';
            } else if (key == 'area' && this.Overlay[key].getArea()) {
                var sqm = this.Overlay[key].getArea();
                var measurements = [];
                var sq = '<sup style="font-size:70%; vertical-align:baseline; position:relative; bottom:1ex;">2</sup>';

                if (sqm < 100000) {
                    measurements.push('<tr><td style="font-family:Arial; font-size:12px; font-weight:bold;">' + sqm.toFixed(0) + ' m' + sq + '</td><td style="padding-left:8px; font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm * 10.76391).toFixed(0) + ' ft.' + sq + '</td></tr>');
                }
                if (sqm < 100000) {
                    measurements.push('<tr><td style="font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 10000).toFixed(2) + ' ha</td><td style="padding-left:8px; font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 4046.85642).toFixed(2) + ' acres</td></tr>');
                }
                else if (sqm >= 100000 && sqm < 1000000) {
                    measurements.push('<tr><td style="font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 10000).toFixed(1) + ' ha</td><td style="padding-left:8px; font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 4046.85642).toFixed(1) + ' acres</td></tr>');
                }
                else if (sqm >= 100000 && sqm < 10000000000) {
                    measurements.push('<tr><td style="font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 10000).toFixed(0) + ' ha</td><td style="padding-left:8px; font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 4046.85642).toFixed(0) + ' acres</td></tr>');
                }
                if (sqm >= 100000 && sqm < 100000000) {
                    measurements.push('<tr><td style="font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 1000000).toFixed(2) + ' km' + sq + '</td><td style="padding-left:8px; font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 2589988.11).toFixed(2) + ' mi.' + sq + '</td></tr>');
                }
                else if (sqm >= 100000000) {
                    measurements.push('<tr><td style="font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 1000000).toFixed(0) + ' km' + sq + '</td><td style="padding-left:8px; font-family:Arial; font-size:12px; font-weight:bold;">' + (sqm / 2589988.11).toFixed(0) + ' mi.' + sq + '</td></tr>');
                }
                result += '<table cellspacing="0" cellpadding="1">' + measurements.join('') + '</table>';
                result += '<span style="font-size:10px; color:#666666;">(da Google, &#177;0.3%)</span>';
            }
        }
        if (result && this.ResultBox[key]) {
            this.ResultBox[key].innerHTML = result;
        }
    }
}

gv_tracklinks_html = '';
gv_dimmed_color = '#aaaaaa';
function GV_Add_Track_to_Tracklist(opts) {
    if (!opts || !opts['name'] || !$('gv_tracklist_links')) {
        return false;
    }
    if (self.gv_options && gv_options['tracklist_options'] && gv_options['tracklist_options']['tracklist'] === false) {
        return false;
    }
    var map_name = (self.gv_options && gv_options['map']) ? gv_options['map'] : 'gmap';
    var map = eval('self.' + map_name);
    if (!$('gv_track_tooltip')) {
        gv_track_tooltip = GV_Initialize_Track_Tooltip(map);
    } // initialize it if it hasn't been done yet
    var tracklinks_id = (opts['div_id'] && $(opts['div_id'])) ? opts['div_id'] : 'gv_tracklist_links';
    if (!$(tracklinks_id)) {
        return false;
    }
    var tracklinks = $(tracklinks_id);
    if (self.gv_options && gv_options['tracklist_options'] && gv_options['tracklist_options']['max_width'] > 0 && tracklinks_id == 'gv_tracklist_links') {
        tracklinks.style.maxWidth = gv_options['tracklist_options']['max_width'] + 'px';
    }
    if (self.gv_options && gv_options['tracklist_options'] && gv_options['tracklist_options']['width'] > 0 && tracklinks_id == 'gv_tracklist_links') {
        tracklinks.style.width = gv_options['tracklist_options']['width'] + 'px';
    }
    var tracklist_background_color = ($('gv_tracklist')) ? GV_Color_Hex2CSS($('gv_tracklist').style.backgroundColor).replace(/ /g, '') : GV_Color_Hex2CSS('#FFFFFF').replace(/ /g, '');
    var this_color_as_css = GV_Color_Hex2CSS(GV_Color_Name2Hex(opts['color'])).replace(/ /g, '');
    var alternate_color = (tracklist_background_color == 'rgb(204,204,204)') ? '#999999' : '#CCCCCC';
    if (tracklist_background_color == this_color_as_css) {
        opts['color'] = alternate_color;
    }
    // if (opts['desc']) { opts['desc'] = opts['desc'].replace(/"/g,'&amp;quot;'); }

    if (opts['number'] && !opts['id']) {
        opts['id'] = 'trk[' + opts['number'] + ']';
    }
    var show_desc = (self.gv_options && gv_options['tracklist_options'] && gv_options['tracklist_options']['desc']) ? true : false;
    var info_id = opts['id'].replace(/^trk\[/, 'trk_info[');
    var id_escaped = opts['id'].replace(/'/g, "\\'");
    var info_id_htmlescaped = info_id.replace(/"/g, "&quot;");
    var tooltips = (self.gv_options && gv_options['tracklist_options'] && gv_options['tracklist_options']['tooltips'] === false) ? false : true;
    var tracklist_tooltip_show = (tooltips) ? 'GV_Create_Track_Tooltip(gmap,' + info_id_htmlescaped + ');' : '';
    var tracklist_tooltip_hide = (tooltips) ? 'GV_Hide_Track_Tooltip();' : '';
    var zoom_link = '';
    if (gv_options['tracklist_options'] && gv_options['tracklist_options']['zoom_links'] !== false) {
        if (eval('self.' + info_id) && eval(info_id + "['bounds']")) {
            opts['bounds'] = eval(info_id + "['bounds']");
        } // backhandedly get the bounds from the track id
        if (opts['bounds'] && (opts['bounds']['n'] || opts['bounds']['s']) && (opts['bounds']['e'] || opts['bounds']['w'])) {
            var bounds = new GLatLngBounds(new GLatLng(opts['bounds']['s'], opts['bounds']['w']), new GLatLng(opts['bounds']['n'], opts['bounds']['e']));
            var center_lat = bounds.getCenter().lat();
            var center_lon = bounds.getCenter().lng();
            var size = new GSize(map.getSize().width - 50, map.getSize().height - 50);
            var zoom = map.getBoundsZoomLevel(bounds, size); // allow for a little margin
            zoom_link = '<img src="http://maps.gpsvisualizer.com/google_maps/tracklist_goto.gif" width="9" height="9" border="0" alt="[zoom]" title="zoom to this track" onclick="' + map_name + '.setCenter(new GLatLng(' + center_lat + ',' + center_lon + '),' + zoom + ');" style="padding-left:3px; cursor:crosshair;">';
        }
    }
    var display_color = (eval('self.' + opts['id']) && eval(opts['id'] + '.gv_hidden')) ? gv_dimmed_color : opts['color'];
    var html = '';
    html += '<div class="gv_tracklist_item"><table cellspacing="0" cellpadding="0" border="0">';
    html += '<tr valign="top">';
    html += '<td class="gv_tracklist_item_name" nowrap>' + opts['bullet'].replace(/ <\//g, '&nbsp;</').replace(/ $/, '&nbsp;') + '</td>'
    html += '<td class="gv_tracklist_item_name">';
    if (show_desc) {
        html += '<span id="' + opts['id'] + '_label" style="color:' + display_color + ';" onclick="GV_Toggle_Track_And_Label(' + map_name + ',\'' + id_escaped + '\',\'' + opts['color'] + '\');" onmouseover="this.style.textDecoration=\'underline\'; ' + tracklist_tooltip_show + '" onmouseout="this.style.textDecoration=\'none\'; ' + tracklist_tooltip_hide + '" title="click to hide/show this track">' + opts['name'] + '</span>' + zoom_link;
    } else {
        html += '<span id="' + opts['id'] + '_label" style="color:' + display_color + ';" onclick="GV_Toggle_Track_And_Label(' + map_name + ',\'' + id_escaped + '\',\'' + opts['color'] + '\');" onmouseover="this.style.textDecoration=\'underline\'; ' + tracklist_tooltip_show + '" onmouseout="this.style.textDecoration=\'none\'; ' + tracklist_tooltip_hide + '" title="' + opts['desc'].replace(/"/g, "&quot;").replace(/(<br>|<\/p>)/, ' ').replace(/<[^>]*>/g, '') + '">' + opts['name'] + '</span>' + zoom_link;
    }
    html += '</td>';
    html += '</tr>';
    if (show_desc && opts['desc']) {
        html += '<tr valign="top"><td></td><td class="gv_tracklist_item_desc">' + opts['desc'] + '</td></tr>';
    }
    html += '</table></div>';
    if (opts['finish_later']) {
        tracklinks.innerHTML += ' ';
        gv_tracklinks_html += html;
    } else {
        tracklinks.innerHTML += html;
        GV_Finish_Tracklist();
    }
}

function GV_Finish_Tracklist() {
    if (!$('gv_tracklist_links')) {
        return false;
    }
    if (gv_tracklinks_html) {
        $('gv_tracklist_links').innerHTML += gv_tracklinks_html;
    }
    if (self.gv_options && gv_options['tracklist_options'] && gv_options['tracklist_options']['max_width'] && gv_options['tracklist_options']['id'] && $(gv_options['tracklist_options']['id'] + '_table')) {
        var tracklist_table = $(gv_options['tracklist_options']['id'] + '_table');
        if (tracklist_table.clientWidth > gv_options['tracklist_options']['max_width']) {
            tracklist_table.style.width = gv_options['tracklist_options']['max_width'] + 'px';
        }
    }
    if (self.gv_options && gv_options['tracklist_options'] && gv_options['tracklist_options']['max_height'] && gv_options['tracklist_options']['id'] && $(gv_options['tracklist_options']['id'])) {
        var tracklist = $(gv_options['tracklist_options']['id']);
        if (tracklist.clientHeight > gv_options['tracklist_options']['max_height']) {
            tracklist.style.height = gv_options['tracklist_options']['max_height'] + 'px';
        }
    }
}

function GV_Show_Track(track_index) {
    GV_Toggle_Track(track_index, true);
}
function GV_Hide_Track(track_index) {
    GV_Toggle_Track(track_index, false);
}
function GV_Show_All_Tracks() {
    GV_Toggle_All_Tracks(true);
}
function GV_Hide_All_Tracks() {
    GV_Toggle_All_Tracks(false);
}

function GV_Toggle_Track(track_index, force) {
    if (!self.trk || !self.trk_info || !self.gmap) {
        return false;
    }
    var color = (trk_info[track_index] && trk_info[track_index]['color']) ? trk_info[track_index]['color'] : '#ffffff';
    GV_Toggle_Overlays(gmap, eval('trk[' + track_index + ']'), force); // pass the track object
    GV_Toggle_Label_Opacity('trk[' + track_index + ']', color, force); // pass the track_id
}

function GV_Toggle_All_Tracks(force) {
    if (!self.trk || !self.trk_info || !self.gmap) {
        return false;
    }
    for (var t in trk) {
        if (trk[t] && trk_info[t]) {
            GV_Toggle_Track_And_Label(gmap, 'trk[' + t + ']', trk_info[t]['color'], force);
        }
    }
}

function GV_Toggle_Track_And_Label(map, id, color, force) {
    if (!color) { // older versions of this function only had two parameters
        color = id;
        id = map;
        map = gmap;
    }
    GV_Toggle_Overlays(map, eval(id), force);
    GV_Toggle_Label_Opacity(id, color, force);
}

function GV_Toggle_Overlays(map, overlay_array, force) {
    if (!map || !overlay_array) {
        return false;
    }
    if (google_api_version >= 2) {
        if (!overlay_array.gv_hidden && force) {
            return false; // do nothing; it's not hidden and we're trying to turn it on
        } else if (overlay_array.gv_hidden && force === false) {
            return false; // do nothing; it IS hidden and we're trying to turn it off
        }
        if (overlay_array.gv_hidden) {
            if (!overlay_array.gv_oor) { // don't turn it on if it's "out of range"
                for (var j = 0; j < overlay_array.length; j++) {
                    map.addOverlay(overlay_array[j]);
                    if (overlay_array[j].label_object) {
                        map.addOverlay(overlay_array[j].label_object);
                    }
                }
            }
            overlay_array.gv_hidden = false;
        } else {
            for (var j = 0; j < overlay_array.length; j++) {
                map.removeOverlay(overlay_array[j]);
                if (overlay_array[j].label_object) {
                    map.removeOverlay(overlay_array[j].label_object);
                }
            }
            overlay_array.gv_hidden = true;
        }
    } else {
        for (var j = 0; j < overlay_array.length; j++) {
            var item = overlay_array[j];
            if (eval(item.drawElement)) {
                if (item.drawElement.style.display == 'none') {
                    item.drawElement.style.display = '';
                }
                else {
                    item.drawElement.style.display = 'none';
                }
            } else if (eval(item.images)) {
                for (var i = 0; i < item.images.length; i++) {
                    if (item.images[i].style.display == 'none') {
                        item.images[i].style.display = '';
                    }
                    else {
                        item.images[i].style.display = 'none';
                    }
                }
            }
        }
    }
}

function GV_Toggle_Label_Opacity(id, original_color, force) { // for track labels in the tracklist
    var label = $(id + '_label');
    if (!label || !label.style) {
        return false;
    }
    var t = eval(id);
    if (!t.length) {
        return false;
    } // t is the track array itself
    if ((!t.gv_hidden && force) || (t.gv_hidden && force === false)) {
        return false;
    }
    if (t.gv_hidden) {
        label.style.color = gv_dimmed_color;
    }
    else {
        label.style.color = original_color;
    }
}

function GV_Toggle(id) {
    if ($(id).style.display == 'none') {
        $(id).style.display = '';
    } else {
        $(id).style.display = 'none';
    }
}
function GV_Windowshade_Setup(handle_id, box_id) {
    if ($(handle_id) && $(box_id)) {
        GEvent.addDomListener($(handle_id), "dblclick", function() {
            if ($(box_id).style.visibility == 'hidden') {
                $(box_id).style.visibility = 'visible';
                $(box_id).style.display = 'block';
            } else {
                $(handle_id).style.width = ($(box_id).parentNode.clientWidth - 2) + 'px'; // -2 for the border
                $(box_id).style.visibility = 'hidden';
                $(box_id).style.display = 'none';
            }
        });
    }
}

function GV_Folder_Collapse_Toggle(folder_index, force_show) {
    if (!self.gv_marker_list_folder_state) {
        return false;
    }
    var fn = GV_Get_Folder_Number(folder_index);
    if (!fn) {
        return false;
    }
    var open_graphic = 'http://maps.gpsvisualizer.com/google_maps/folder_triangle_open.gif';
    var closed_graphic = 'http://maps.gpsvisualizer.com/google_maps/folder_triangle_closed.gif';
    if ((gv_marker_list_folder_state[fn].collapsed || force_show === true) && force_show !== false) {
        $('folder_collapse_' + fn).src = open_graphic;
        $('folder_contents_' + fn).style.display = 'block';
        gv_marker_list_folder_state[fn].collapsed = false;
    } else {
        $('folder_collapse_' + fn).src = closed_graphic;
        $('folder_contents_' + fn).style.display = 'none';
        gv_marker_list_folder_state[fn].collapsed = true;
    }
}
function GV_Folder_Visibility_Toggle(folder_index, force_show) {
    if (!self.gv_marker_list_folder_state) {
        return false;
    }
    var fn = GV_Get_Folder_Number(folder_index);
    if (!fn) {
        return false;
    }
    if ((gv_marker_list_folder_state[fn].hidden || force_show === true) && force_show !== false) {
        $('folder_checkbox_' + fn).checked = true;
        GV_Adjust_Opacity('folder_contents_' + fn, 100);
        GV_Adjust_Opacity('folder_name_' + fn, 100);
        GV_Toggle_Markers_With_Text({show: true, field: 'folder', pattern: gv_marker_list_folder_name[fn], simple_match: true});
        gv_marker_list_folder_state[fn].hidden = false;
    } else {
        $('folder_checkbox_' + fn).checked = false;
        GV_Adjust_Opacity('folder_contents_' + fn, 30);
        GV_Adjust_Opacity('folder_name_' + fn, 40);
        GV_Toggle_Markers_With_Text({show: false, field: 'folder', pattern: gv_marker_list_folder_name[fn], simple_match: true});
        gv_marker_list_folder_state[fn].hidden = true;
    }
}

function GV_Get_Folder_Number(index) {
    var fn = null;
    if (index.toString().match(/^\d+$/)) {
        fn = index;
    } else if (self.gv_marker_list_folder_name && gv_marker_list_folder_name.length) {
        for (var j = 1; j < gv_marker_list_folder_name.length; j++) {
            if (index == gv_marker_list_folder_name[j]) {
                fn = j;
                j = gv_marker_list_folder_name.length + 1; // break out of the loop
            }
        }
    }
    return (fn);
}

function GV_Filter_Tracks(map, info) {
    if (info == null || info == undefined || typeof(info) != 'object') {
        return false;
    }
    var trk_array_name = (self.gv_options && gv_options['track_array'] && eval('self.' + gv_options['track_array'])) ? gv_options['track_array'] : 'trk';
    var min_lat = map.getBounds().getSouthWest().lat();
    var min_lon = map.getBounds().getSouthWest().lng();
    var max_lat = map.getBounds().getNorthEast().lat();
    var max_lon = map.getBounds().getNorthEast().lng();
    if (max_lon < min_lon) {
        min_lon = -180;
        max_lon = 180;
    } // Date Line weirdness
    if (info[1]) { // new style: the bounds are in trk_info[n]['bounds'], and there's (almost) always a trk_info[1].
        for (var t in info) {
            if (info[t]['bounds']) {
                if (info[t]['bounds']['e'] < min_lon || info[t]['bounds']['w'] > max_lon || info[t]['bounds']['n'] < min_lat || info[t]['bounds']['s'] > max_lat) {
                    GV_Track_OutOfRange(map, eval(trk_array_name + '[\'' + t + '\']'), true);
                } else {
                    GV_Track_OutOfRange(map, eval(trk_array_name + '[\'' + t + '\']'), false);
                }
            }
        }
    } else { // old style: bounds are directly in the trk_info hash
        for (var t in info) {
            if (info[t]['e'] && info[t]['w'] && info[t]['n'] && info[t]['s']) {
                if (info[t]['e'] < min_lon || info[t]['w'] > max_lon || info[t]['n'] < min_lat || info[t]['s'] > max_lat) {
                    GV_Track_OutOfRange(map, eval(t), true);
                } else {
                    GV_Track_OutOfRange(map, eval(t), false);
                }
            }
        }
    }
}
function GV_Track_OutOfRange(map, trk_array, is_oor) {
    if (is_oor) {
        if (!trk_array.gv_hidden) { // if it's already hidden, there's nothing to do
            for (var j in trk_array) {
                if (trk_array[j].getLatLng || trk_array[j].getVertexCount) {
                    map.removeOverlay(trk_array[j]);
                }
            }
        }
        trk_array.gv_oor = true;
    } else {
        if (trk_array.gv_oor) { // only add it if it was previously out of range
            if (!trk_array.gv_hidden) { // if it's supposed to be hidden, don't show it
                for (var j in trk_array) {
                    if (trk_array[j].getLatLng || trk_array[j].getVertexCount) {
                        map.addOverlay(trk_array[j]);
                    }
                }
            }
        }
        trk_array.gv_oor = false;
    }
}

function GV_Remove_All_Markers(opts) {
    if (!opts) {
        opts = {};
    } // in case NOTHING is passed into the function
    var map_name = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    var marker_array_name = (opts['marker_array']) ? opts['marker_array'] : 'wpts';
    var marker_array = eval(marker_array_name);
    if (!marker_array) {
        return false;
    }
    for (var j = 0; j < marker_array.length; j++) {
        var w = marker_array[j];
        map.removeOverlay(w);
        if (w.label_object) {
            map.removeOverlay(w.label_object);
        }
    }
    if (gv_marker_list_exists) {
        gv_marker_list_html = '';
        gv_marker_list_folders = [];
        gv_marker_list_count = 0;
        GV_Marker_List();
    }
}

function GV_Filter_Markers_With_Text(opts) {
    // Works with these fields: name,desc,url,shortdesc,icon,color,image,thumbnail,type,lat,lon,coords,folder
    if (typeof(opts) == 'string') {
        var p = opts;
        opts = {};
        opts['pattern'] = p;
    }
    if (!opts) {
        opts = {};
    } // in case NOTHING is passed into the function for unfiltering
    var map_name = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    var marker_array_name = (opts['marker_array']) ? opts['marker_array'] : 'wpts';
    var marker_array = eval(marker_array_name);
    if (!marker_array) {
        return false;
    }
    var pattern = (opts['pattern']) ? opts['pattern'] : '';
    var simple_match = (opts['simple_match']) ? true : false; // if this is true, the pattern is NOT evaluated as a RegExp
    var autozoom = (opts['autozoom']) ? true : (opts['zoom']) ? true : false; // autozoom, basically
    var zoom_adjustment = (opts['zoom_adjustment']) ? opts['zoom_adjustment'] : 0;
    var labels_visible = (opts['labels']) ? true : false;
    var field = (opts['field']) ? opts['field'] : 'namedesc'; // if no field specified, search name AND desc
    if (field.indexOf('lat') == 0) {
        field = 'latitude';
    }
    else if (field.indexOf('lon') == 0 || field.indexOf('lng') == 0) {
        field = 'longitude';
    }
    else if (field.indexOf('desc') == 0) {
        field = 'desc';
    }
    if (field == 'namedesc' && !simple_match) {
        pattern = pattern.replace('$', '[$|\\t]');
    }
    var pattern_regexp = new RegExp(pattern, 'i');

    var update_list = (gv_marker_filter_options['update_list']) ? true : false;
    var sort_list_by_distance = (gv_marker_filter_options['sort_list_by_distance']) ? true : false;
    var limit = (gv_marker_filter_options['limit'] > 0) ? gv_marker_filter_options['limit'] : 0;

    if (gv_marker_list_exists && update_list) {
        gv_marker_list_html = '';
        gv_marker_list_folders = [];
        gv_marker_list_count = 0;
    }

    var to_be_added = [];
    var new_bounds = null;

    // Check for an open window; we'll re-open it if needed
    var gv_infowindow_open = false;
    if (map.getInfoWindow() && map.getInfoWindow().isHidden() === false) {
        gv_infowindow_open = true;
    }

    // First, remove all points
    for (var j = 0; j < marker_array.length; j++) {
        var w = marker_array[j];
        map.removeOverlay(w);
        if (w.label_object) {
            map.removeOverlay(w.label_object);
        }
        var text = '';
        /* possible future feature: make this a loop that can have multiple field/pattern combos */
        if (field == 'latitude') {
            text = w.coords.lat().toString();
        }
        else if (field == 'longitude') {
            text = w.coords.lng().toString();
        }
        else if (field == 'coords') {
            text = w.coords.toString();
        }
        else if (field == 'namedesc') {
            text = w.name + "\t" + w.desc;
        }
        else if (w[field]) {
            text = w[field];
        }
        if ((pattern == '' || (simple_match && text == pattern) || (!simple_match && text.match(pattern_regexp))) && !w.gv_hidden && !w.gv_oor) {
            if (limit > 0 || sort_list_by_distance) {
                w.dist_from_center = map.getCenter().distanceFrom(w.coords);
                var key = (w.dist_from_center / 10000000).toFixed(8);
                to_be_added.push(key + ' ' + j);
            } else {
                to_be_added.push(j);
            }
            if (new_bounds) {
                new_bounds.extend(w.coords);
            } else {
                new_bounds = new GLatLngBounds(w.coords, w.coords);
            }
        } else {
            if (gv_infowindow_open && typeof(self.gv_open_infowindow_index) != 'undefined' && gv_open_infowindow_index == j) {
                gv_infowindow_open = false;
            }
        }
        /* end possible future feature loop */
    }
    // Then, sort them by distance if that's what the options say
    if (limit > 0 || (sort_list_by_distance && update_list)) {
        to_be_added = to_be_added.sort();
        if (limit > 0 && limit < to_be_added.length) {
            to_be_added.length = limit;
        }
        for (var j = 0; j < to_be_added.length; j++) {
            var parts = to_be_added[j].split(' ');
            to_be_added[j] = parseInt(parts[1]);
        }
        if (!sort_list_by_distance) { // back to the original order
            to_be_added = to_be_added.sort(function(a, b) {
                return(a - b)
            });
        }
    }
    // Then, put the appropriate ones back
    for (var j = 0; j < to_be_added.length; j++) {
        var w = marker_array[to_be_added[j]];
        if (!w.gv_hidden) { // if it's supposed to be hidden, don't show it
            map.addOverlay(w);
            if (w.label_object) {
                map.addOverlay(w.label_object);
                GV_Label_Visibility(marker_array_name, to_be_added[j], labels_visible);
            }
        }
        if (gv_marker_list_exists && update_list && (w.type != 'tickmark' || gv_marker_list_options['include_tickmarks']) && (w.type != 'trackpoint' || gv_marker_list_options['include_trackpoints']) && !w.nolist) {
            if (typeof(w.list_html) != 'undefined') {
                if (w.folder) {
                    gv_marker_list_folders[w.folder] = (gv_marker_list_folders[w.folder]) ? gv_marker_list_folders[w.folder] + w.list_html : w.list_html;
                } else {
                    gv_marker_list_html += w.list_html;
                }
                gv_marker_list_count += 1;
            }
        }
    }
    if (autozoom && new_bounds) {
        var new_zoom = map.getBoundsZoomLevel(new_bounds);
        new_zoom = (new_zoom > 15) ? 15 : new_zoom + zoom_adjustment;
        map.setCenter(new_bounds.getCenter(), new_zoom);
    }

    // These are normally done as part of the GV_Process_Markers meta-function, but they need to be done manually here:
    if (gv_marker_list_exists) {
        GV_Marker_List();
    }
    // If labels are supposed to be visible, we need to re-do them after filtering
    if (self.gv_labels_are_visible && gv_labels_are_visible && self.gv_options && gv_options['marker_array']) {
        GV_Toggle_All_Labels(gv_options['marker_array'], true);
    }
    if (gv_infowindow_open && gv_open_infowindow_index != null) {
        GEvent.trigger(marker_array[gv_open_infowindow_index], "click");
        GEvent.trigger(marker_array[gv_open_infowindow_index], "mouseout");
    }

}
function GV_Filter_Waypoints_With_Text(opts) {
    GV_Filter_Markers_With_Text(opts);
}

function GV_Toggle_Markers_With_Text(opts) {


    // Works with these fields: name,desc,url,shortdesc,icon,color,image,thumbnail,type,lat,lon,coords,folder
    if (!opts) {
        opts = {};
    } // in case NOTHING is passed into the function for unfiltering
    var map_name = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    var marker_array_name = (opts['marker_array']) ? opts['marker_array'] : 'wpts';
    var marker_array = eval(marker_array_name);
    if (!marker_array) {
        return false;
    }
    var pattern = (opts['pattern']) ? opts['pattern'] : '';
    var pattern_regexp = new RegExp(pattern, 'i');
    var simple_match = (opts['simple_match']) ? true : false; // if this is true, the pattern is NOT evaluated as a RegExp
    var field = (opts['field']) ? opts['field'] : 'namedesc'; // if no field specified, search name AND desc
    if (field.indexOf('lat') == 0) {
        field = 'latitude';
    }
    else if (field.indexOf('lon') == 0 || field.indexOf('lng') == 0) {
        field = 'longitude';
    }
    else if (field.indexOf('desc') == 0) {
        field = 'desc';
    }

    var visible = (opts['show'] === false) ? false : true;

    var to_be_changed = [];

    // First, look at all points (and remove them!)
    for (var j = 0; j < marker_array.length; j++) {
        var w = marker_array[j];
        var text = '';
        if (field == 'latitude') {
            text = w.coords.lat().toString();
        }
        else if (field == 'longitude') {
            text = w.coords.lng().toString();
        }
        else if (field == 'coords') {
            text = w.coords.toString();
        }
        else if (field == 'namedesc') {
            text = w.name + "\t" + w.desc;
        }
        else if (w[field]) {
            text = w[field];
        }
        if (pattern == '' || (simple_match && text == pattern) || (!simple_match && text.match(pattern_regexp))) {
            map.removeOverlay(w);
            if (w.label_object) {
                map.removeOverlay(w.label_object);
            }
            to_be_changed.push(j);
        }
    }
    // Then, do the appropriate operation to the appropriate points
    for (var j = 0; j < to_be_changed.length; j++) {
        var w = marker_array[to_be_changed[j]];
        if (visible && !w.gv_hidden && !w.gv_oor) {
            map.addOverlay(w);
            if (w.label_object) {
                map.addOverlay(w.label_object);
            }
            // w.gv_hidden = false;
        } else {
            map.removeOverlay(w);
            if (w.label_object) {
                map.removeOverlay(w.label_object);
            }
            // w.gv_hidden = true;
        }
    }

    // If labels are supposed to be visible, we need to re-do them after filtering
    if (self.gv_labels_are_visible && gv_labels_are_visible && self.gv_options && gv_options['marker_array']) {
        GV_Toggle_All_Labels(gv_options['marker_array'], true);
    }
}
function GV_Toggle_Waypoints_With_Text(opts) {
    GV_Toggle_Markers_With_Text(opts);
}

function GV_Filter_Markers_In_View(map, marker_array) {
    var limit = (gv_marker_filter_options['limit'] > 0) ? gv_marker_filter_options['limit'] : 0;
    var update_list = (gv_marker_filter_options['update_list']) ? true : false;
    var sort_list_by_distance = (gv_marker_filter_options['sort_list_by_distance']) ? true : false;
    var min_lat = map.getBounds().getSouthWest().lat();
    var min_lon = map.getBounds().getSouthWest().lng();
    var max_lat = map.getBounds().getNorthEast().lat();
    var max_lon = map.getBounds().getNorthEast().lng();

    var gv_marker_filter_current_position = map.getCenter();
    var gv_marker_filter_current_zoom = map.getZoom();
    gv_marker_filter_moved_enough = true;
    if (gv_marker_filter_options['movement_threshold'] && self.gv_marker_filter_last_position) {
        var width_in_meters = map.getCenter().distanceFrom(new GLatLng(map.getCenter().lat(), map.getBounds().getNorthEast().lng())) * 2;
        var moved_in_meters = gv_marker_filter_current_position.distanceFrom(gv_marker_filter_last_position);
        var fraction_moved = moved_in_meters / width_in_meters;
        var pixels_moved = map.getContainer().clientWidth * fraction_moved;
        if (gv_marker_filter_options['movement_threshold']) {
            if (gv_marker_filter_current_zoom != gv_marker_filter_last_zoom || (pixels_moved == 0 && self.gv_options && gv_options['dynamic_data'] && gv_dynamic_file_index >= 0 && gv_options['dynamic_data'][gv_dynamic_file_index] && gv_options['dynamic_data'][gv_dynamic_file_index]['reload_on_request'])) {
                gv_marker_filter_moved_enough = true;
            } else { // zoom was the same
                if (pixels_moved < parseFloat(gv_marker_filter_options['movement_threshold'])) {
                    gv_marker_filter_moved_enough = false;
                }
            }
        }
    }

    if (gv_marker_filter_moved_enough) {
        if (max_lon < min_lon) {
            min_lon = -180;
            max_lon = 180;
        } // Date Line weirdness
        if (gv_marker_list_exists && update_list) {
            gv_marker_list_html = '';
            gv_marker_list_folders = [];
            gv_marker_list_count = 0;
        }
        var min_zoom = (gv_marker_filter_options['min_zoom'] && gv_marker_filter_options['min_zoom'] > 0) ? gv_marker_filter_options['min_zoom'] : 0;
        var show_no_markers = (map.getZoom() >= min_zoom) ? false : true;
        var to_be_added = [];

        // First, remove all points
        for (var j = 0; j < marker_array.length; j++) {
            var w = marker_array[j];
            map.removeOverlay(w);
            if (w.label_object) {
                map.removeOverlay(w.label_object);
            }
            w.gv_oor = true; // oor = out of range
            // While iterating through the points looking for ones to remove, record which ones should eventually be put back
            if (show_no_markers) {
                // we're in "don't show anything" mode, so there's no point in doing calculations
            } else {
                if (w.coords.lng() < min_lon || w.coords.lng() > max_lon || w.coords.lat() < min_lat || w.coords.lat() > max_lat) {
                    // do nothing; it's out of range
                } else {
                    if (!limit && !update_list) { // add everything that's in the viewport
                        to_be_added.push(j);
                    } else {
                        if (limit > 0 || sort_list_by_distance) {
                            w.dist_from_center = map.getCenter().distanceFrom(w.coords);
                            var key = (w.dist_from_center / 10000000).toFixed(8);
                            to_be_added.push(key + ' ' + j);
                        } else {
                            to_be_added.push(j);
                        }
                    }
                }
            }
        }
        // Then, put the appropriate ones back
        if (show_no_markers) {
            if (map.getZoom() < min_zoom) {
                if (gv_marker_list_options['zoom_message'] && gv_marker_list_options['zoom_message'] != '') {
                    gv_marker_list_html = gv_marker_list_options['zoom_message'];
                } else if (gv_marker_filter_options['zoom_message'] && gv_marker_filter_options['zoom_message'] != '') {
                    gv_marker_list_html = gv_marker_filter_options['zoom_message'];
                } else {
                    gv_marker_list_html = '<p>Zoom in further to see markers.</p>';
                }
            }
        } else {
            if (limit > 0 || (sort_list_by_distance && update_list)) {
                to_be_added = to_be_added.sort();
                if (limit > 0 && limit < to_be_added.length) {
                    to_be_added.length = limit;
                }
                for (var j = 0; j < to_be_added.length; j++) {
                    var parts = to_be_added[j].split(' ');
                    to_be_added[j] = parseInt(parts[1]);
                }
                if (!sort_list_by_distance) { // back to the original order
                    to_be_added = to_be_added.sort(function(a, b) {
                        return(a - b)
                    });
                }
            }
            for (var j = 0; j < to_be_added.length; j++) {
                var w = marker_array[to_be_added[j]];
                if (!w.gv_hidden) { // if it's supposed to be hidden, don't show it
                    map.addOverlay(w);
                    w.gv_oor = false; // oor = out of range
                    if (w.label_object) {
                        map.addOverlay(w.label_object);
                    }
                }
                if (gv_marker_list_exists && update_list && (w.type != 'tickmark' || gv_marker_list_options['include_tickmarks']) && (w.type != 'trackpoint' || gv_marker_list_options['include_trackpoints']) && !w.nolist) {
                    if (limit > 0 && gv_marker_list_count >= limit) {
                        // do nothing; we're over the limit
                    } else {
                        if (typeof(w.list_html) != 'undefined') {
                            if (w.folder) {
                                gv_marker_list_folders[w.folder] = (gv_marker_list_folders[w.folder]) ? gv_marker_list_folders[w.folder] + w.list_html : w.list_html;
                            } else {
                                gv_marker_list_html += w.list_html;
                            }
                            gv_marker_list_count += 1;
                        }
                    }
                }
            }
        }
        gv_marker_filter_last_position = map.getCenter();
        gv_marker_filter_last_zoom = map.getZoom();
    } // end if (gv_marker_filter_moved_enough)
}
function GV_Filter_Waypoints_In_View(map, marker_array) {
    GV_Filter_Markers_In_View(map, marker_array);
}


function GV_Filter_Waypoints(map, marker_array) { // For backwards compatibility
    GV_Process_Markers(map, marker_array);
}

function GV_Recenter_Per_URL(opts) {
    if (!opts) {
        return false;
    }
    var new_center = null;
    var new_zoom = null;
    var hide_crosshair = false;
    var map_name = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    var map = eval('self.' + map_name);
    if (map == null) {
        if (self.gmap) {
            map = gmap;
        } else {
            return false;
        }
    }
    var center_key = (opts['center_key']) ? opts['center_key'] : 'center';
    var zoom_key = (opts['zoom_key']) ? opts['zoom_key'] : 'zoom';
    var default_zoom = (opts['default_zoom']) ? opts['default_zoom'] : null;
    var maptype_key = (opts['maptype_key']) ? opts['maptype_key'] : 'maptype';
    var partial_match = (opts['partial_match'] === false) ? false : true;
    var open_info_window = (opts['open_info_window']) ? true : false;
    var marker_array_name = (gv_options['marker_array']) ? gv_options['marker_array'] : 'wpts';
    var type_aliases = (opts['type_alias']) ? opts['type_alias'] : null;
    var center_window = (opts['center_window']) ? opts['center_window'] : '';

    var open_info_window_pattern = new RegExp('[&\\?\#](?:open.?)?info.?window=([^&]+)', 'i');
    if (window.location.toString().match(open_info_window_pattern)) {
        var open_info_window_match = open_info_window_pattern.exec(window.location.toString());
        if (open_info_window_match && open_info_window_match[1].match(/^[1ty]/i)) {
            open_info_window = true;
        } else if (open_info_window_match && open_info_window_match[1].match(/^[0fn]/i)) {
            open_info_window = false;
        }
    }

    var maptype_pattern = new RegExp('[&\\?\#]' + maptype_key + '=([A-Z0-9_]+)', 'i');
    if (window.location.toString().match(maptype_pattern)) {
        var maptype_match = maptype_pattern.exec(window.location.toString());
        if (maptype_match && maptype_match[1]) { // the appropriate variable was found in the URL's query string
            var t = uri_unescape(maptype_match[1]);
            if (type_aliases) {
                if (type_aliases[t.toLowerCase()]) {
                    t = type_aliases[t.toLowerCase()];
                }
            }
            if (eval('self.' + t.toUpperCase())) {
                map.setMapType(eval(t.toUpperCase())); // this one is independent of center & zoom; just do it
                if (gv_maptypecontrol && gv_maptypecontrol.control_style && gv_maptypecontrol.control_style == 'menu' && $('gv_map_selector')) {
                    var type_menu = $('gv_map_selector');
                    for (var i = 0; i < type_menu.length; i++) {
                        if (type_menu[i].value != '' && type_menu[i].value.toUpperCase() == t.toUpperCase()) {
                            type_menu.selectedIndex = i;
                        }
                    }
                }
            }
        }
    }

    var zoom_pattern = new RegExp('[&\\?\#]' + zoom_key + '=([0-9]+)', 'i');
    if (window.location.toString().match(zoom_pattern)) {
        var zoom_match = zoom_pattern.exec(window.location.toString());
        if (zoom_match && zoom_match[1]) { // the appropriate variable was found in the URL's query string
            var z = uri_unescape(zoom_match[1]);
            if (z.match(/[0-9]/)) {
                z = parseFloat(z);
                if (z > 19) {
                    z = 19;
                }
                if (z < 0) {
                    z = 0;
                }
                new_zoom = z;
            }
        }
    }

    var center_window_pattern = new RegExp('[&\\?\#](center.?window|note)=([^&]+)', 'i');
    var center_window_html = '';
    if (window.location.toString().match(center_window_pattern)) {
        var center_window_match = center_window_pattern.exec(window.location.toString());
        if (center_window_match && center_window_match[2]) { // the appropriate variable was found in the URL's query string
            center_window_html = uri_unescape(center_window_match[2]);
        }
    }

    var center_pattern = new RegExp('[&\\?\#]' + center_key + '=([^&]+)', 'i');
    if (window.location.toString().match(center_pattern)) {
        var center_match = center_pattern.exec(window.location.toString());
        if (center_match && center_match[1]) {
            // the appropriate variable was found in the URL's query string
            var c = center_match[1].replace(/\+/g, ' ');
            c = uri_unescape(c);
            if (c.match(/^wpts?\[/)) {
                if (eval('self.' + c) && eval('self.' + c + '.coords')) {
                    new_center = eval('self.' + c + '.coords');
                    if (open_info_window) {
                        window.setTimeout('GEvent.trigger(' + c + ',"click")', 500);
                    }
                }
            } else if (c.match(/^trk?\[/)) {
                if (eval('self.' + c)) {
                    GV_Autozoom({map: map_name, adjustment: 0}, eval(c));
                    if (new_zoom) {
                        map.setZoom(new_zoom);
                    }
                    return true;
                }
            } else {
                new_center = GV_Marker_Coordinates({map: map_name, marker_array: marker_array_name, pattern: c, partial_match: partial_match, open_info_window: open_info_window});
                if (c && !new_center) { // the GV_Marker_Coordinates function can detect both marker names and numeric coordinates; this was neither, so geocode it
                    var geocoder = new GClientGeocoder();
                    if (opts['custom_function']) {
                        geocoder.getLocations(c, eval(opts['custom_function']));
                    } else {
                        geocoder.getLatLng(c, function(coords) {
                            map.setCenter(coords);
                        });
                    }
                }
            }
        }
    }
    if (new_center) {
        if (default_zoom && !new_zoom) {
            new_zoom = default_zoom;
        }
        if (new_zoom) {
            map.setCenter(new_center, new_zoom);
        } else {
            map.setCenter(new_center);
        }
        if (hide_crosshair && $('gv_crosshair')) {
            $('gv_crosshair').style.display = 'none';
            gv_crosshair_temporarily_hidden = true;
        }
        if (center_window_html) {
            map.openInfoWindowHtml(map.getCenter(), center_window_html);
        }
        return true;
    } else if (new_zoom) {
        map.setZoom(new_zoom);
        if (center_window_html) {
            map.openInfoWindowHtml(map.getCenter(), center_window_html);
        }
        return true;
    } else {
        if (center_window_html) {
            map.openInfoWindowHtml(map.getCenter(), center_window_html);
        }
        return false;
    }
}


function GV_Marker_Coordinates(opts) {
    if (!opts) {
        return false;
    }
    var map_name = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    var marker_array_name = (opts['marker_array']) ? opts['marker_array'] : 'wpts';
    var marker_array = eval('self.' + marker_array_name);
    if (marker_array == null) {
        return false;
    }
    var partial_match = (opts['partial_match'] === false) ? false : true;
    var open_info_window = (opts['open_info_window']) ? true : false;
    var pattern = opts['pattern'];
    var field = (opts['field']) ? opts['field'] : 'name';
    var new_center = null;

    var coordinate_pattern = new RegExp('([NS])? *(-?[0-9]*\.?[0-9]*) *([NS])? *, *([EW])? *(-?[0-9]*\.?[0-9]*) *([EW])?', 'i');
    if (pattern.match(coordinate_pattern)) { // the query looks like a pair of numeric coordinates
        var coordinate_match = coordinate_pattern.exec(pattern.toUpperCase());
        if (coordinate_match && coordinate_match[2] != null && coordinate_match[5] != null) {
            var lat = parseFloat(coordinate_match[2]);
            var lon = parseFloat(coordinate_match[5]);
            if (coordinate_match[1] == 'S' || coordinate_match[3] == 'S') {
                lat = 0 - lat;
            }
            if (coordinate_match[4] == 'W' || coordinate_match[6] == 'W') {
                lon = 0 - lon;
            }
            if (Math.abs(lat) <= 90 && Math.abs(lon) <= 180) {
                new_center = new GLatLng(lat, lon);
            }
        }
    } else { // they didn't request a coordinate pair, so look to see if any waypoint's name matches the query
        if (partial_match) {
            for (var i = 0; i < marker_array.length; i++) { // the "!new_center" clause prevents it from finding multiple markers
                if (!new_center && marker_array[i][field].toLowerCase().indexOf(pattern.toLowerCase()) > -1) {
                    new_center = marker_array[i].coords;
                    hide_crosshair = true;
                    // the info window is opened HERE, in this informational subroutine, because this is where we know which marker to pop open
                    if (open_info_window) {
                        window.setTimeout('GEvent.trigger(' + marker_array_name + '[' + i + '],"click")', 500);
                    }
                }
            }
        } else {
            for (var i = 0; i < marker_array.length; i++) {
                if (!new_center && marker_array[i][field] == pattern) {
                    new_center = marker_array[i].coords;
                    hide_crosshair = true;
                    // the info window is opened HERE, in this informational subroutine, because this is where we know which marker to pop open
                    if (open_info_window) {
                        window.setTimeout('GEvent.trigger(' + marker_array_name + '[' + i + '],"click")', 500);
                    }
                }
            }
        }
    }
    return new_center;
}

function GV_Center_On_Marker(pattern_or_opts, opts) {
    if (typeof(pattern_or_opts) == 'object') { // figure out whether the first argument is an array or a simple pattern
        opts = pattern_or_opts;
        pattern = opts['pattern'];
    } else { // it's a string or number
        if (pattern_or_opts) {
            pattern = pattern_or_opts;
        } else {
            return false;
        }
    }
    var map_name = (opts && opts['map']) ? opts['map'] : 'gmap';
    var map = eval('self.' + map_name);
    if (!map) {
        return false;
    }
    var marker_array_name = (opts && opts['marker_array']) ? opts['marker_array'] : 'wpts';
    var marker_array = eval('self.' + marker_array_name);
    if (marker_array == null) {
        return false;
    }
    var partial_match = (opts && opts['partial_match'] === false) ? false : true;
    var open_info_window = (opts && opts['open_info_window']) ? true : false;
    var zoom = (opts && opts['zoom']) ? opts['zoom'] : null;
    var field = (opts && opts['field']) ? opts['field'] : 'name';

    var new_center = GV_Marker_Coordinates({map: map_name, marker_array: marker_array_name, field: field, pattern: pattern, partial_match: partial_match, open_info_window: open_info_window});
    if (new_center) {
        map.setCenter(new_center);
        if (zoom) {
            map.setZoom(zoom);
        }
    }
}

function GV_Center_On_Address(opts) {
    var map = eval('self.' + opts['map']);
    if (map == null) {
        if (self.gmap) {
            map = gmap;
            opts['map'] = 'gmap';
        } else {
            return false;
        }
    }
    if (!$(opts['input_box'])) {
        return false;
    }
    var address = $(opts['input_box']).value;
    if (!address) {
        return false;
    }
    var message_box = (opts && opts['message_box'] && $(opts['message_box'])) ? $(opts['message_box']) : null;
    var centered = false; // an easy way for the program to tell if it turned out to be simple coordinates
    var coordinate_pattern = new RegExp('^ *([NS])? *(-?[0-9]*\\.?[0-9]*) *([NS])? *, *([EW])? *(-?[0-9]*\\.?[0-9]*) *([EW])? *$', 'i');
    if (address.match(coordinate_pattern)) { // the query looks like a pair of numeric coordinates
        var coordinate_match = coordinate_pattern.exec(address.toUpperCase());
        if (coordinate_match && coordinate_match[2] != null && coordinate_match[5] != null) {
            var lat = parseFloat(coordinate_match[2]);
            var lon = parseFloat(coordinate_match[5]);
            if (coordinate_match[1] == 'S' || coordinate_match[3] == 'S') {
                lat = 0 - lat;
            }
            if (coordinate_match[4] == 'W' || coordinate_match[6] == 'W') {
                lon = 0 - lon;
            }
            if (Math.abs(lat) <= 90 && Math.abs(lon) <= 180) {
                map.setCenter(new GLatLng(lat, lon)); // don't change the zoom level
                centered = true;
                if (message_box) {
                    message_box.innerHTML = 'Re-centering on ' + lat + ', ' + lon;
                }
            }
        }
    }
    if (!centered) {
        var geocoder = new GClientGeocoder();
        gv_center_on_address = opts; // need to make the options into a global variable
        geocoder.getLocations(address, GV_Center_On_Address2);
    }
}

function GV_Center_On_Address2(info) {
    var map = eval('self.' + gv_center_on_address['map']);
    if (map == null) {
        if (self.gmap) {
            map = gmap;
            gv_center_on_address['map'] = 'gmap';
        } else {
            return false;
        }
    }
    var message_box = (self.gv_center_on_address && gv_center_on_address['message_box'] && $(gv_center_on_address['message_box'])) ? $(gv_center_on_address['message_box']) : null;
    var zoom_to_result = (self.gv_center_on_address && gv_center_on_address['zoom'] === false) ? false : true;
    var found_template = (self.gv_center_on_address && gv_center_on_address['found_template']) ? gv_center_on_address['found_template'] : 'Google found: <b>{address}</b> ({latitude},{longitude})  [precision:{precision}]';
    var unfound_template = (self.gv_center_on_address && gv_center_on_address['unfound_template']) ? gv_center_on_address['unfound_template'] : 'Google could not locate "{input}".';
    var google_precision = ['not found', 'country', 'state/region', 'county/municipality', 'city', 'zip/postcode', 'street', 'intersection', 'address', 'feature/building'];
    var accuracy_as_zoom = [null, 5, 6, 9, 11, 13, 14, 15, 15];
    //                      nf    na st co ci  zip str int add
    if (info && info.Placemark && info.Placemark[0].Point) {
        var coords = new GLatLng(info.Placemark[0].Point.coordinates[1], info.Placemark[0].Point.coordinates[0]);
        var address = (info.Placemark[0].address) ? info.Placemark[0].address : '';
        var zoom = (map.getZoom() >= 5) ? map.getZoom() : 5;
        var accuracy = info.Placemark[0].AddressDetails.Accuracy;
        if (info.Placemark[0].AddressDetails) {
            if (info.Placemark[0].address.indexOf(',') > -1 && accuracy == 1) {
                accuracy = undefined;
            } // countries don't have commas in them; therefore it wasn't really a country that the geocoder found!
            zoom = accuracy_as_zoom[accuracy]; // if it's undefined, that's okay, it just won't zoom
        }
        if (zoom_to_result) {
            map.setCenter(coords, zoom);
        } else {
            map.setCenter(coords);
        }
        var found = found_template.replace(/{address}/i, address).replace(/{latitude}/i, info.Placemark[0].Point.coordinates[1]).replace(/{longitude}/i, info.Placemark[0].Point.coordinates[0]).replace(/{address}/i, address).replace(/{precision}/i, google_precision[accuracy]);
        if (message_box) {
            message_box.innerHTML = found;
        }
    } else if (info && info.name) {
        var unfound = unfound_template.replace(/{input}/i, info.name);
        if (message_box) {
            message_box.innerHTML = unfound;
        }
    }
    gv_center_on_address = [];
}

function GV_Setup_Crosshair(map, opts) {
    if (!opts['crosshair_container_id']) {
        opts['crosshair_container_id'] = 'gv_crosshair_container';
    }
    if (!opts['crosshair_graphic_id']) {
        opts['crosshair_graphic_id'] = 'gv_crosshair';
    }
    if (!opts['crosshair_width']) {
        opts['crosshair_width'] = 15;
    }
    if (!opts['center_coordinates_id']) {
        opts['center_coordinates_id'] = 'gv_center_coordinates';
    }

    GV_Recenter_Crosshair(map, opts['crosshair_container_id'], opts['crosshair_width']);
    GV_Show_Center_Coordinates(map, opts['center_coordinates_id']);
    GEvent.addListener(map, "moveend", function() {
        // GV_Show_Hidden_Crosshair(map,opts['crosshair_graphic_id']);
        GV_Show_Center_Coordinates(map, opts['center_coordinates_id']);
    });
    // if (opts['fullscreen'] || opts['full_screen']) {
    GEvent.addListener(map, "resize", function() {
        GV_Recenter_Crosshair(map, opts['crosshair_container_id'], opts['crosshair_width']);
        // GV_Show_Hidden_Crosshair(map,opts['crosshair_graphic_id']);
        GV_Show_Center_Coordinates(map, opts['center_coordinates_id']);
    });
    // }

}

function GV_Show_Center_Coordinates(map, id) {
    if ($(id)) {
        var lat = parseFloat(map.getCenter().lat()).toFixed(5);
        var lng = parseFloat(map.getCenter().lng()).toFixed(5);
        $(id).innerHTML = 'Coordinate centrali: <span id="center_coordinate_pair">' + lat + ',' + lng + '</span>';
    }
    gv_last_center = map.getCenter(); // this will come in handy; make sure it happens AFTER the crosshair is potentially unhidden
}

var gv_crosshair_temporarily_hidden = true;
function GV_Show_Hidden_Crosshair(map, id) {
    // only do something upon the FIRST movement of the map, or when it's been hidden, e.g. because of a centering action
    if (self.gv_crosshair_temporarily_hidden && (!self.gv_last_center || gv_last_center.lat() != map.getCenter().lat() || gv_last_center.lng() != map.getCenter().lng())) {
        if (self.gv_hidden_crosshair_is_still_hidden && gv_hidden_crosshair_is_still_hidden == true) {
            // don't do anything
        } else {
            $(id).style.display = 'block';
            gv_crosshair_temporarily_hidden = false;
        }
    }
}

function GV_Recenter_Crosshair(map, container_id, crosshair_size) {
    if ($(container_id)) {
        if ($(container_id).align) { // in the older version, we always used align="left" in the DIV
            $(container_id).style.position = 'absolute';
            $(container_id).style.top = Math.round(map.getContainer().clientHeight / 2 - (crosshair_size / 2)) + 'px';
            $(container_id).style.left = Math.round(map.getContainer().clientWidth / 2 - (crosshair_size / 2)) + 'px';
        } else {
            var x = Math.round(map.getContainer().clientWidth / 2 - (crosshair_size / 2));
            var y = Math.round(map.getContainer().clientHeight / 2 - (crosshair_size / 2));
            GV_Place_Control(map, container_id, G_ANCHOR_TOP_LEFT, x, y);
        }
    }
}

function GV_Place_Control(map, control_id, anchor, x, y) {
    if ($(control_id)) {
        $(control_id).style.display = 'block';
        var gv_position = new GControlPosition(anchor, new GSize(x, y));
        gv_position.apply($(control_id));
        map.getContainer().appendChild($(control_id));
    }
}
function GV_Remove_Control(control_id) {
    if ($(control_id)) {
        $(control_id).style.display = 'none';
        $(control_id).parentNode.removeChild($(control_id));
    }
}

function GV_Adjust_Opacity(id, opacity) {
    // This is an all-purpose function for using style sheets to adjust the opacity of ANY object with an id
    opacity = parseFloat(opacity);
    if (opacity < 1) {
        opacity = opacity * 100;
    }
    if ($(id)) {
        var thing = $(id);
        thing.opacity = opacity;
        thing.style.filter = 'alpha(opacity=' + opacity + ')';
        thing.style.MozOpacity = opacity / 100;
        thing.style.KhtmlOpacity = opacity / 100;
    }
}

function GV_Background_Opacity(map, opacity) {
    if (opacity == null) {
        return;
    }
    if (opacity <= 0) {
        opacity = 0;
    }
    else if (opacity > 1) {
        opacity = opacity / 100;
    }
    gv_bg_opacity = opacity; // this is a global and absolutely necessary for the "moveend" listener
    var screen_opacity = 1 - opacity; // this function alters the screen, not the bg
    var id = 'gv_opacity_screen';
    // from http://blogs.msdn.com/b/ie/archive/2010/08/17/ie9-opacity-and-alpha.aspx
    var useOpacity = (typeof document.createElement("div").style.opacity != 'undefined');
    var useFilter = !useOpacity && (typeof document.createElement("div").style.filter != 'undefined');

    if (!$(id)) {
        var new_screen = document.createElement("div");
        new_screen.id = id;
        new_screen.className = 'gmnoprint gv_opacity_screen';
        new_screen.style.position = "absolute";
        // new_screen.style.backgroundColor = "#ffffff";
        if (useOpacity) {
            new_screen.style.opacity = screen_opacity;
        }
        if (useFilter) {
            new_screen.style.filter = "alpha(opacity=" + screen_opacity * 100 + ")";
        }
        new_screen.style.KhtmlOpacity = screen_opacity;
        new_screen.style.MozOpacity = screen_opacity;
        map.getPane(G_MAP_MAP_PANE).appendChild(new_screen);
        GEvent.addListener(map, "moveend", function() {
            GV_Background_Opacity(map, eval('gv_bg_opacity'));
        });
    }
    GV_Update_Background_Screen(map, id, screen_opacity);
}
function GV_Update_Background_Screen(map, id, screen_opacity) {
    if (map && $(id) && screen_opacity != null) {
        var screen = $(id);
        var screen_scale = 3; // how many times bigger than the current width & height is it?
        var map_width = map.getContainer().clientWidth;
        var map_height = map.getContainer().clientHeight;
        var nw = new GLatLng(map.getBounds().getNorthEast().lat(), map.getBounds().getSouthWest().lng());
        var offset = map.fromLatLngToDivPixel(nw);
        // from http://blogs.msdn.com/b/ie/archive/2010/08/17/ie9-opacity-and-alpha.aspx
        var useOpacity = (typeof document.createElement("div").style.opacity != 'undefined');
        var useFilter = !useOpacity && (typeof document.createElement("div").style.filter != 'undefined');
        screen.style.visibility = (screen_opacity == 0) ? 'hidden' : 'visible';
        if (useOpacity) {
            screen.style.opacity = screen_opacity;
        }
        if (useFilter) {
            screen.style.filter = "alpha(opacity=" + screen_opacity * 100 + ")";
        }
        screen.style.KhtmlOpacity = screen_opacity;
        screen.style.MozOpacity = screen_opacity;
        screen.style.left = offset.x - (map_width * ((screen_scale - 1) / 2)) + "px";
        screen.style.top = offset.y - (map_height * ((screen_scale - 1) / 2)) + "px";
        screen.style.width = screen_scale * map_width + "px";
        screen.style.height = screen_scale * map_height + "px";
        if ($('gv_opacity_selector')) {
            var op_menu = $('gv_opacity_selector');
            for (var i = 0; i < op_menu.length; i++) {
                if (op_menu[i].value != '' && op_menu[i].value == Math.round(100 * (1 - screen_opacity)) / 100) {
                    op_menu.selectedIndex = i;
                }
            }
        }
    }
}

function GV_Fill_Window_With_Map(mapdiv_id) {
    if (!$(mapdiv_id)) {
        return false;
    }
    var mapdiv = $(mapdiv_id);
    var window_size = GV_GetWindowSize();
    mapdiv.style.position = 'absolute';
    mapdiv.style.left = '0px';
    mapdiv.style.top = '0px';
    mapdiv.style.width = window_size[0] + 'px';
    mapdiv.style.height = window_size[1] + 'px';
}
function GV_GetWindowSize() {
    // from http://www.quirksmode.org/viewport/compatibility.html
    var x, y;
    if (window.innerHeight) { // all except Explorer
        x = window.innerWidth;
        y = window.innerHeight;
    } else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
        x = document.documentElement.clientWidth;
        y = document.documentElement.clientHeight;
    } else if (document.body) { // other Explorers
        x = document.body.clientWidth;
        y = document.body.clientHeight;
    }
    return [x, y];
}

function GV_MouseWheel(e) {
    if (!e || !self.gmap) {
        return false;
    }
    if (e.detail) { // Firefox
        if (e.detail < 0) {
            gmap.zoomIn();
        }
        else if (e.detail > 0) {
            gmap.zoomOut();
        }
    } else if (e.wheelDelta) { // IE
        if (e.wheelDelta > 0) {
            gmap.zoomIn();
        }
        else if (e.wheelDelta < 0) {
            gmap.zoomOut();
        }
    }
}
function GV_MouseWheelReverse(e) {
    if (!e || !self.gmap) {
        return false;
    }
    if (e.detail) { // Firefox
        if (e.detail < 0) {
            gmap.zoomOut();
        }
        else if (e.detail > 0) {
            gmap.zoomIn();
        }
    } else if (e.wheelDelta) { // IE
        if (e.wheelDelta > 0) {
            gmap.zoomOut();
        }
        else if (e.wheelDelta < 0) {
            gmap.zoomIn();
        }
    }
}

function GV_Format_Time(ts, tz, tz_text, twelve_hour) {
    ts = (ts) ? ts.toString() : ''; // time stamp
    tz = (tz) ? parseFloat(tz) : 0; // time zone
    twelve_hour = (twelve_hour) ? true : false; // time zone
    if (!ts) {
        return '';
    }
    var d = null;
    if (ts.match(/^\d\d\d\d\d\d\d\d\d\d$/)) {
        var unix_time = parseFloat(ts) + (tz * 3600);
        var d = new Date(unix_time * 1000);
    } else {
        var parts = [];
        parts = ts.match(/([12][90]\d\d)[^0-9]?(\d\d+)[^0-9]?(\d\d+)[^0-9]*(\d\d+)[^0-9]?(\d\d+)[^0-9]?(\d\d+)/);
        if (parts) {
            var unix_time = Date.parse(parts[2] + '/' + parts[3] + '/' + parts[1] + ' ' + parts[4] + ':' + parts[5] + ':' + parts[6]);
            unix_time = (unix_time / 1000) + (tz * 3600);
            var d = new Date(unix_time * 1000);
        }
    }
    if (d) {
        var hour = d.getHours();
        var ampm = '';
        if (twelve_hour) {
            ampm = (hour >= 12) ? ' PM' : ' AM';
            if (hour > 12) {
                hour -= 12;
            }
        }
        var tz_text = (tz_text) ? ' (' + tz_text + ')' : '';
        if (twelve_hour) {
            return (d.getMonth() + 1) + "/" + (d.getDate()) + "/" + d.getFullYear().toString().substring(2) + ", " + hour + ":" + (d.getMinutes() < 10 ? "0" : "") + d.getMinutes() + ":" + (d.getSeconds() < 10 ? "0" : "") + d.getSeconds() + ampm + tz_text;
        } else {
            return d.getFullYear() + "-" + (d.getMonth() < 9 ? "0" : "") + (d.getMonth() + 1) + (d.getDate() < 10 ? "0" : "") + "-" + d.getDate() + " " + ((hour < 10 && !twelve_hour) ? "0" : "") + hour + ":" + (d.getMinutes() < 10 ? "0" : "") + d.getMinutes() + ":" + (d.getSeconds() < 10 ? "0" : "") + d.getSeconds() + tz_text;
        }
    } else {
        return '';
    }
}

function GV_Color_Hex2CSS(c) {
    if (c == null) {
        return null;
    }
    var rgb = [];
    rgb = c.match(/([A-F0-9]{2})([A-F0-9]{2})([A-F0-9]{2})/i);
    if (rgb) {
        return ('rgb(' + parseInt(rgb[1], 16) + ',' + parseInt(rgb[2], 16) + ',' + parseInt(rgb[3], 16) + ')');
    } else {
        return (c.replace(/ +/g, ''));
    }
}

function GV_Color_Name2Hex(color_name) { // uses the global variable called "gv_named_html_colors"
    if (color_name.match(/^#[A-F0-9][A-F0-9][A-F0-9][A-F0-9][A-F0-9][A-F0-9]$/i)) {
        return color_name;
    }
    var color_name_trimmed = color_name.replace(/^\#/, '');
    if (gv_named_html_colors[color_name_trimmed]) {
        return gv_named_html_colors[color_name_trimmed];
    } else {
        return color_name_trimmed;
    }
}
function GV_Define_Named_Colors() {
    var c = [];
    c['aliceblue'] = '#f0f8ff';
    c['antiquewhite'] = '#faebd7';
    c['aqua'] = '#00ffff';
    c['aquamarine'] = '#7fffd4';
    c['azure'] = '#f0ffff';
    c['beige'] = '#f5f5dc';
    c['bisque'] = '#ffe4c4';
    c['black'] = '#000000';
    c['blanchedalmond'] = '#ffebcd';
    c['blue'] = '#0000ff';
    c['blueviolet'] = '#8a2be2';
    c['brown'] = '#a52a2a';
    c['burlywood'] = '#deb887';
    c['cadetblue'] = '#5f9ea0';
    c['chartreuse'] = '#7fff00';
    c['chocolate'] = '#d2691e';
    c['coral'] = '#ff7f50';
    c['cornflowerblue'] = '#6495ed';
    c['cornsilk'] = '#fff8dc';
    c['crimson'] = '#dc143c';
    c['cyan'] = '#00ffff';
    c['darkblue'] = '#00008b';
    c['darkcyan'] = '#008b8b';
    c['darkgoldenrod'] = '#b8860b';
    c['darkgray'] = '#a9a9a9';
    c['darkgreen'] = '#006400';
    c['darkkhaki'] = '#bdb76b';
    c['darkmagenta'] = '#8b008b';
    c['darkolivegreen'] = '#556b2f';
    c['darkorange'] = '#ff8c00';
    c['darkorchid'] = '#9932cc';
    c['darkred'] = '#8b0000';
    c['darksalmon'] = '#e9967a';
    c['darkseagreen'] = '#8fbc8f';
    c['darkslateblue'] = '#483d8b';
    c['darkslategray'] = '#2f4f4f';
    c['darkturquoise'] = '#00ced1';
    c['darkviolet'] = '#9400d3';
    c['deeppink'] = '#ff1493';
    c['deepskyblue'] = '#00bfff';
    c['dimgray'] = '#696969';
    c['dodgerblue'] = '#1e90ff';
    c['firebrick'] = '#b22222';
    c['floralwhite'] = '#fffaf0';
    c['forestgreen'] = '#228b22';
    c['fuchsia'] = '#ff00ff';
    c['gainsboro'] = '#dcdcdc';
    c['ghostwhite'] = '#f8f8ff';
    c['gold'] = '#ffd700';
    c['goldenrod'] = '#daa520';
    c['gray'] = '#808080';
    c['grey'] = '#808080';
    c['green'] = '#008000';
    c['greenyellow'] = '#adff2f';
    c['honeydew'] = '#f0fff0';
    c['hotpink'] = '#ff69b4';
    c['indianred'] = '#cd5c5c';
    c['indigo'] = '#4b0082';
    c['ivory'] = '#fffff0';
    c['khaki'] = '#f0e68c';
    c['lavender'] = '#e6e6fa';
    c['lavenderblush'] = '#fff0f5';
    c['lawngreen'] = '#7cfc00';
    c['lemonchiffon'] = '#fffacd';
    c['lightblue'] = '#add8e6';
    c['lightcoral'] = '#f08080';
    c['lightcyan'] = '#e0ffff';
    c['lightgoldenrodyellow'] = '#fafad2';
    c['lightgreen'] = '#90ee90';
    c['lightgrey'] = '#d3d3d3';
    c['lightpink'] = '#ffb6c1';
    c['lightsalmon'] = '#ffa07a';
    c['lightseagreen'] = '#20b2aa';
    c['lightskyblue'] = '#87cefa';
    c['lightslategray'] = '#778899';
    c['lightsteelblue'] = '#b0c4de';
    c['lightyellow'] = '#ffffe0';
    c['lime'] = '#00ff00';
    c['limegreen'] = '#32cd32';
    c['linen'] = '#faf0e6';
    c['magenta'] = '#ff00ff';
    c['maroon'] = '#800000';
    c['mediumaquamarine'] = '#66cdaa';
    c['mediumblue'] = '#0000cd';
    c['mediumorchid'] = '#ba55d3';
    c['mediumpurple'] = '#9370db';
    c['mediumseagreen'] = '#3cb371';
    c['mediumslateblue'] = '#7b68ee';
    c['mediumspringgreen'] = '#00fa9a';
    c['mediumturquoise'] = '#48d1cc';
    c['mediumvioletred'] = '#c71585';
    c['midnightblue'] = '#191970';
    c['mintcream'] = '#f5fffa';
    c['mistyrose'] = '#ffe4e1';
    c['moccasin'] = '#ffe4b5';
    c['navajowhite'] = '#ffdead';
    c['navy'] = '#000080';
    c['oldlace'] = '#fdf5e6';
    c['olive'] = '#808000';
    c['olivedrab'] = '#6b8e23';
    c['orange'] = '#ffa500';
    c['orangered'] = '#ff4500';
    c['orchid'] = '#da70d6';
    c['palegoldenrod'] = '#eee8aa';
    c['palegreen'] = '#98fb98';
    c['paleturquoise'] = '#afeeee';
    c['palevioletred'] = '#db7093';
    c['papayawhip'] = '#ffefd5';
    c['peachpuff'] = '#ffdab9';
    c['peru'] = '#cd853f';
    c['pink'] = '#ffc0cb';
    c['plum'] = '#dda0dd';
    c['powderblue'] = '#b0e0e6';
    c['purple'] = '#800080';
    c['red'] = '#ff0000';
    c['rosybrown'] = '#bc8f8f';
    c['royalblue'] = '#4169e1';
    c['saddlebrown'] = '#8b4513';
    c['salmon'] = '#fa8072';
    c['sandybrown'] = '#f4a460';
    c['seagreen'] = '#2e8b57';
    c['seashell'] = '#fff5ee';
    c['sienna'] = '#a0522d';
    c['silver'] = '#c0c0c0';
    c['skyblue'] = '#87ceeb';
    c['slateblue'] = '#6a5acd';
    c['slategray'] = '#708090';
    c['snow'] = '#fffafa';
    c['springgreen'] = '#00ff7f';
    c['steelblue'] = '#4682b4';
    c['tan'] = '#d2b48c';
    c['teal'] = '#008080';
    c['thistle'] = '#d8bfd8';
    c['tomato'] = '#ff6347';
    c['turquoise'] = '#40e0d0';
    c['violet'] = '#ee82ee';
    c['wheat'] = '#f5deb3';
    c['white'] = '#ffffff';
    c['whitesmoke'] = '#f5f5f5';
    c['yellow'] = '#ffff00';
    c['yellowgreen'] = '#9acd32';
    return (c);
}
function GV_KML_Icon_Anchors(md) { // md = marker_data
    if (md['icon'].match(/mapfiles\/kml\/pal\d\//i)) {
        if (md['icon'].match(/^http:\/\/(maps|www)\.(google|gstatic)\.\w+.*\/.*?mapfiles\/kml\/pal5\/icon13\.png/i)) {
            md['icon_anchor'] = [11, 24];
        } // small flag
        else if (md['icon'].match(/^http:\/\/(maps|www)\.(google|gstatic)\.\w+.*\/.*?mapfiles\/kml\/pal5\/icon14\.png/i)) {
            md['icon_anchor'] = [21, 27];
        } // small pushpin
    } else if (md['icon'].match(/^http:\/\/(maps|www)\.(google|gstatic)\.\w+.*\/.*?mapfiles\/(ms\/m?icons|kml)/i)) {
        if (md['icon'].match(/\bpushpin\.png$/i)) {
            md['icon_anchor'] = [10, 31];
        } else if (md['icon'].match(/\bdot\.png$|(red|orange|yellow|green|blue|purple|l(igh)?tblue?|pink)\.png$|\/paddle\//i)) {
            md['icon_anchor'] = [15, 31];
        } else if (md['icon'].match(/\/poi\.png$/i)) {
            md['icon_anchor'] = [24, 24];
        } else if (md['icon'].match(/\/flag\.png$/i)) {
            md['icon_anchor'] = [12, 31]; // 
        }
    } else if (md['icon'].match(/^http:\/\/(maps|www)\.(google|gstatic)\.\w+.*\/.*?mapfiles\/.*pushpin\/.*\.png/i)) {
        md['icon_anchor'] = [10, 31];
    }
    return (md);
}
function GV_Define_Garmin_Icons(icon_dir, garmin_icon_set) {
    var garmin_codes = new Array(
            'Airport', 'Amusement Park', 'Anchor', 'Anchor Prohibited', 'Animal Tracks', 'ATV'
            , 'Bait and Tackle', 'Ball Park', 'Bank', 'Bar', 'Beach', 'Beacon', 'Bell', 'Bike Trail', 'Block, Blue', 'Block, Green', 'Block, Red', 'Boat Ramp', 'Bowling', 'Bridge', 'Building', 'Buoy, White', 'Big Game', 'Blind', 'Blood Trail'
            , 'Campground', 'Car', 'Car Rental', 'Car Repair', 'Cemetery', 'Church', 'Circle with X', 'Circle With X', 'Circle, Blue', 'Circle, Green', 'Circle, Red', 'City (Capitol)', 'City (Large)', 'City (Medium)', 'City (Small)', 'City Hall', 'Civil', 'Coast Guard', 'Controlled Area', 'Convenience Store', 'Crossing', 'Cover', 'Covey'
            , 'Dam', 'Danger Area', 'Department Store', 'Diamond, Blue', 'Diamond, Green', 'Diamond, Red', 'Diver Down Flag 1', 'Diver Down Flag 2', 'Dock', 'Dot', 'Dot, White', 'Drinking Water', 'Dropoff'
            , 'Exit'
            , 'Fast Food', 'Fishing Area', 'Fishing Hot Spot Facility', 'Fitness Center', 'Flag', 'Flag, Blue', 'Flag, Green', 'Flag, Red', 'Forest', 'Food Source', 'Furbearer'
            , 'Gas Station', 'Geocache', 'Geocache Found', 'Ghost Town', 'Glider Area', 'Golf Course', 'Ground Transportation'
            , 'Heliport', 'Horn', 'Hunting Area'
            , 'Ice Skating', 'Information'
            , 'Levee', 'Library', 'Light', 'Live Theater', 'Lodge', 'Lodging', 'Letterbox Cache'
            , 'Man Overboard', 'Marina', 'Medical Facility', 'Mile Marker', 'Military', 'Mine', 'Movie Theater', 'Museum', 'Multi Cache', 'Multi-Cache'
            , 'Navaid, Amber', 'Navaid, Black', 'Navaid, Blue', 'Navaid, Green', 'Navaid, Green/Red', 'Navaid, Green/White', 'Navaid, Orange', 'Navaid, Red', 'Navaid, Red/Green', 'Navaid, Red/White', 'Navaid, Violet', 'Navaid, White', 'Navaid, White/Green', 'Navaid, White/Red'
            , 'Oil Field', 'Oval, Blue', 'Oval, Green', 'Oval, Red'
            , 'Parachute Area', 'Park', 'Parking Area', 'Pharmacy', 'Picnic Area', 'Pin, Blue', 'Pin, Green', 'Pin, Red', 'Pizza', 'Police Station', 'Post Office', 'Private Field', 'Puzzle Cache'
            , 'Radio Beacon', 'Rectangle, Blue', 'Rectangle, Green', 'Rectangle, Red', 'Reef', 'Residence', 'Restaurant', 'Restricted Area', 'Restroom', 'RV Park'
            , 'Scales', 'Scenic Area', 'School', 'Seaplane Base', 'Shipwreck', 'Shopping Center', 'Short Tower', 'Shower', 'Ski Resort', 'Skiing Area', 'Skull and Crossbones', 'Soft Field', 'spacer.gif', 'Square, Blue', 'Square, Green', 'Square, Red', 'Stadium', 'Stump', 'Summit', 'Swimming Area', 'Small Game'
            , 'Tall Tower', 'Telephone', 'Toll Booth', 'TracBack Point', 'Trail Head', 'Triangle, Blue', 'Triangle, Green', 'Triangle, Red', 'Truck Stop', 'Tunnel', 'Tree Stand', 'Treed Quarry', 'Truck'
            , 'Ultralight Area', 'Upland Game'
            , 'Water Hydrant', 'Water Source', 'Waypoint', 'Weed Bed', 'Wrecker', 'Waterfowl', 'Winery'
            , 'Zoo'
            , 'CoursePoint:1st_Category', 'CoursePoint:2nd_Category', 'CoursePoint:3rd_Category', 'CoursePoint:4th_Category', 'CoursePoint:Danger', 'CoursePoint:First_Aid', 'CoursePoint:FirstAid', 'CoursePoint:FirstCat', 'CoursePoint:Food', 'CoursePoint:FourthCat', 'CoursePoint:Generic', 'CoursePoint:Hors_Category', 'CoursePoint:HorsCat', 'CoursePoint:Left', 'CoursePoint:Right', 'CoursePoint:SecondCat', 'CoursePoint:Sprint', 'CoursePoint:Straight', 'CoursePoint:Summit', 'CoursePoint:ThirdCat', 'CoursePoint:Valley', 'CoursePoint:Water'
            );
    var garmin_urls = [];
    var garmin_dir = icon_dir + 'garmin/gpsmap/';
    if (garmin_icon_set == 'mapsource') {
        garmin_dir = icon_dir + 'garmin/mapsource/';
    }
    else if (garmin_icon_set == '24x24') {
        garmin_dir = icon_dir + 'garmin/24x24/';
    }

    for (var i in garmin_codes) {
        garmin_urls[garmin_codes[i]] = [];
        garmin_urls[garmin_codes[i]]['url'] = garmin_dir + garmin_codes[i].replace(/[ :]/g, '_').replace(/\//g, '-') + '.png';
    }
    garmin_urls['Civil']['anchor'] = [4, 16];
    garmin_urls['Flag']['anchor'] = [4, 16];
    garmin_urls['Flag, Blue']['anchor'] = [4, 16];
    garmin_urls['Flag, Green']['anchor'] = [4, 16];
    garmin_urls['Flag, Red']['anchor'] = [4, 16];
    garmin_urls['Pin, Blue']['anchor'] = [1, 14];
    garmin_urls['Pin, Green']['anchor'] = [1, 14];
    garmin_urls['Pin, Red']['anchor'] = [1, 14];
    garmin_urls['Golf Course']['anchor'] = [7, 11];
    garmin_urls['Tall Tower']['anchor'] = [7, 13];
    garmin_urls['Short Tower']['anchor'] = [7, 11];
    garmin_urls['Radio Beacon']['anchor'] = [5, 13];

    return (garmin_urls);
}


/**************************************************
 * Custom map layers:
 * Adapted from Jef Poskanzer's Acme Mapper
 * (http://mapper.acme.com/)
 **************************************************/
if (gv_api_version >= 2) {
    gv_background_maps = [];
    USGS_TOPO_TILES = CustomWMSLayers([{object_name: 'USGS_TOPO_TILES', name: 'Topo', copyright: 'Topo maps by USGS via msrmaps.com', error_message: 'Topo maps unavailable', minResolution: 5, maxResolution: 17, tile_size: 400, base_url: 'http://msrmaps.com/ogcmap6.ashx?version=1.1.1&request=GetMap&styles=&srs=EPSG:4326&format=image/jpeg&bgcolor=0xCCCCCC&exceptions=INIMAGE&layers=DRG'}]);
    USGS_AERIAL_TILES = CustomWMSLayers([{object_name: 'USGS_AERIAL_TILES', name: 'Aerial', copyright: 'Imagery by USGS via msrmaps.com', error_message: 'USGS aerial imagery unavailable', minResolution: 7, maxResolution: 18, tile_size: 400, base_url: 'http://msrmaps.com/ogcmap6.ashx?version=1.1.1&request=GetMap&styles=&srs=EPSG:4326&format=image/jpeg&bgcolor=0xCCCCCC&exceptions=INIMAGE&layers=DOQ'}]);
    USGS_AERIAL_HYBRID_TILES = CustomWMSLayers([{object_name: 'USGS_AERIAL_HYBRID_TILES', name: 'Aerial+G.', copyright: 'Imagery by USGS via msrmaps.com', error_message: 'USGS aerial imagery unavailable', minResolution: 7, maxResolution: 18, tile_size: 256, base_url: 'http://msrmaps.com/ogcmap6.ashx?version=1.1.1&request=GetMap&styles=&srs=EPSG:4326&format=image/jpeg&bgcolor=0xCCCCCC&exceptions=INIMAGE&layers=DOQ', foreground: 'G_HYBRID_MAP'}]);
    NRCAN_TOPO_TILES = CustomWMSLayers([{object_name: 'NRCAN_TOPO_TILES', name: 'NRCan', copyright: 'Maps by NRCan.gc.ca', error_message: 'NRCan maps unavailable', minResolution: 6, maxResolution: 18, tile_size: 600, base_url: 'http://wms.cits.rncan.gc.ca/cgi-bin/cubeserv.cgi?version=1.1.3&request=GetMap&format=image/png&bgcolor=0xFFFFFF&exceptions=application/vnd.ogc.se_inimage&srs=EPSG:4326&layers=PUB_50K:CARTES_MATRICIELLES/RASTER_MAPS'}]);
    NRCAN_TOPO_NAMES_TILES = CustomWMSLayers([{object_name: 'NRCAN_TOPO_NAMES_TILES', name: 'NRCan+', copyright: 'Maps by NRCan.gc.ca', error_message: 'NRCan maps unavailable', minResolution: 11, maxResolution: 18, tile_size: 600, base_url: 'http://wms.cits.rncan.gc.ca/cgi-bin/cubeserv.cgi?version=1.1.3&request=GetMap&format=image/png&bgcolor=0xFFFFFF&exceptions=application/vnd.ogc.se_inimage&srs=EPSG:4326&layers=PUB_50K:CARTES_MATRICIELLES/RASTER_MAPS,TOPONYME_0:BNDT_50K/NTDB_50K'}]);
    NRCAN_TOPO_HYBRID_TILES = CustomWMSLayers([{object_name: 'NRCAN_TOPO_HYBRID_TILES', name: 'NRCan+G.', copyright: 'Topo maps by NRCan.gc.ca', error_message: 'NRCan maps unavailable', minResolution: 11, maxResolution: 18, tile_size: 256, base_url: 'http://wms.cits.rncan.gc.ca/cgi-bin/cubeserv.cgi?version=1.1.3&request=GetMap&format=image/png&bgcolor=0xFFFFFF&exceptions=application/vnd.ogc.se_inimage&srs=EPSG:4326&layers=PUB_50K:CARTES_MATRICIELLES/RASTER_MAPS', foreground: 'G_HYBRID_MAP'}]);
    NEXRAD_TILES = CustomWMSLayers([{object_name: 'NEXRAD_TILES', name: 'NEXRAD', copyright: 'NEXRAD imagery from Iowa Environmental Mesonet', error_message: 'NEXRAD imagery unavailable', minResolution: 3, maxResolution: 14, tile_size: 256, base_url: 'http://mesonet.agron.iastate.edu/cgi-bin/wms/nexrad/n0r.cgi?version=1.1.1&request=GetMap&service=WMS&srs=EPSG:4326&format=image/png&transparent=true&styles=&layers=nexrad-n0r', opacity: 0.7, background: 'G_PHYSICAL_MAP'}]); // NOTE: for combo maps using Google tiles, tile_size MUST be 256!!!
    US_COUNTY_TILES = CustomWMSLayers([{object_name: 'US_COUNTY_TILES', name: 'Counties', copyright: 'U.S. Counties from The National Atlas', error_message: 'National Atlas unavailable', minResolution: 4, maxResolution: 12, tile_size: 128, base_url: 'http://imsref.cr.usgs.gov/wmsconnector/com.esri.wms.Esrimap/USGS_EDC_National_Atlas?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetMap&srs=EPSG:4326&format=PNG&transparent=FALSE&layers=ATLAS_COUNTIES_2001,ATLAS_STATES,ATLAS_STATES_075,ATLAS_STATES_150'}]);
    // US_NATMAP_RELIEF = CustomWMSLayers([{object_name:'US_NATMAP_RELIEF',name:'NatMap relief',copyright:'U.S. relief from The National Atlas',error_message:'National Atlas unavailable',minResolution:4,maxResolution:12,tile_size:400,base_url:'http://imsref.cr.usgs.gov/wmsconnector/com.esri.wms.Esrimap/USGS_EDC_National_Atlas?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetMap&srs=EPSG:4326&format=PNG&Layers=ATLAS_SATELLITE_RELIEF_AK,ATLAS_SATELLITE_RELIEF_HI,ATLAS_SATELLITE_RELIEF_48'}]);
    // US_COUNTY_HYBRID_TILES = CustomWMSLayers([{object_name:'US_COUNTY_HYBRID_TILES',name:'Counties+G.',copyright:'U.S. Counties + Google satellite',error_message:'National Atlas unavailable',minResolution:4,maxResolution:12,tile_size:400,base_url:'http://imsref.cr.usgs.gov/wmsconnector/com.esri.wms.Esrimap/USGS_EDC_National_Atlas?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetMap&srs=EPSG:4326&format=PNG&transparent=TRUE&Layers=ATLAS_COUNTIES_2001,ATLAS_STATES,ATLAS_STATES_075,ATLAS_STATES_150',opacity:1.0,background:'US_NATMAP_RELIEF',bg_opacity:0.25}]);
    // doesn't work well, projection-wise: DEMIS_PHYSICAL_TILES = CustomWMSLayers([{object_name:'DEMIS_PHYSICAL_TILES',name:'DEMIS',copyright:'Map by DEMIS',error_message:'DEMIS server unavailable',minResolution:1,maxResolution:17,tile_size:256,base_url:'http://www2.demis.nl/wms/wms.asp?version=1.1.0&wms=WorldMap&request=GetMap&srs=EPSG:4326&format=jpeg&transparent=false&exceptions=inimage&wrapdateline=true&layers=Bathymetry,Countries,Topography,Coastlines,Waterbodies,Rivers,Streams,Highways,Roads,Railroads,Trails,Hillshading,Borders'}]);
    LANDSAT_TILES = CustomWMSLayers([{object_name: 'LANDSAT_TILES', name: 'Landsat', copyright: 'Map by NASA', error_message: 'OnEarth server unavailable', minResolution: 3, maxResolution: 15, tile_size: 260, base_url: 'http://onearth.jpl.nasa.gov/wms.cgi?request=GetMap&styles=&srs=EPSG:4326&format=image/jpeg&layers=global_mosaic'}]);
    BLUEMARBLE_TILES = CustomWMSLayers([{object_name: 'BLUEMARBLE_TILES', name: 'BlueMarble', copyright: 'Map by NASA', error_message: 'OnEarth server unavailable', minResolution: 3, maxResolution: 8, tile_size: 260, base_url: 'http://onearth.jpl.nasa.gov/wms.cgi?request=GetMap&styles=&srs=EPSG:4326&format=image/jpeg&layers=modis'}]);
    // BLUEMARBLE_TILES = CustomWMSLayers([{object_name:'BLUEMARBLE_TILES',name:'BlueMarble',copyright:'Map by DEMIS',error_message:'DEMIS server unavailable',minResolution:3,maxResolution:8,tile_size:260,base_url:'http://www2.demis.nl/wms/wms.asp?service=WMS&wms=BlueMarble&wmtver=1.0.0&request=GetMap&srs=EPSG:4326&format=jpeg&transparent=false&exceptions=inimage&wrapdateline=true&layers=Earth+Image,Borders'}]);
    DAILY_TERRA_TILES = CustomWMSLayers([{object_name: 'DAILY_TERRA_TILES', name: '"Terra"', copyright: 'Map by NASA', error_message: 'OnEarth server unavailable', minResolution: 3, maxResolution: 10, tile_size: 260, base_url: 'http://onearth.jpl.nasa.gov/wms.cgi?request=GetMap&styles=&srs=EPSG:4326&format=image/jpeg&layers=daily_terra'}]);
    DAILY_AQUA_TILES = CustomWMSLayers([{object_name: 'DAILY_AQUA_TILES', name: '"Aqua"', copyright: 'Map by NASA', error_message: 'OnEarth server unavailable', minResolution: 3, maxResolution: 10, tile_size: 260, base_url: 'http://onearth.jpl.nasa.gov/wms.cgi?request=GetMap&styles=&srs=EPSG:4326&format=image/jpeg&layers=daily_aqua'}]);
    DAILY_MODIS_TILES = CustomWMSLayers([{object_name: 'DAILY_MODIS_TILES', name: 'MODIS', copyright: 'Map by NASA', error_message: 'OnEarth server unavailable', minResolution: 3, maxResolution: 10, tile_size: 260, base_url: 'http://onearth.jpl.nasa.gov/wms.cgi?request=GetMap&styles=&srs=EPSG:4326&format=image/jpeg&layers=daily_planet'}]);
    SRTM_COLOR_TILES = CustomWMSLayers([{object_name: 'SRTM_COLOR_TILES', name: 'SRTM', copyright: 'SRTM elevation data by NASA', error_message: 'SRTM elevation data unavailable', minResolution: 6, maxResolution: 14, tile_size: 260, base_url: 'http://onearth.jpl.nasa.gov/wms.cgi?request=GetMap&srs=EPSG:4326&format=image/jpeg&styles=&layers=huemapped_srtm'}]);
    MYTOPO_TILES = CustomTileBasedLayer({object_name: 'MYTOPO_TILES', short_name: 'MyTopo', long_name: 'MyTopo.com', error_message: 'MyTopo tiles unavailable', min_res: 7, max_res: 16, template: 'http://maps.mytopo.com/gpsvisualizer/tilecache.py/1.0.0/topoG/{Z}/{X}/{Y}.png', copyright: 'Topo maps &#169; <a href="http://www.mytopo.com/?pid=gpsvisualizer" target="_blank" onclick="if(this.href.toString().indexOf(\'&lat=\')>-1){this.href=this.href.toString().replace(/&lat=-?\\d+\\.?\\d*/,\'&lat=\'+' + gv_options['map'] + '.getCenter().lat()).replace(/&lon=-?\\d+\\.?\\d*/,\'&lon=\'+' + gv_options['map'] + '.getCenter().lng());}else{this.href+=\'&lat=\'+' + gv_options['map'] + '.getCenter().lat()+\'&lon=\'+' + gv_options['map'] + '.getCenter().lng();}">MyTopo.com</a>'});
    OPENSTREETMAP_TILES = CustomTileBasedLayer({object_name: 'OPENSTREETMAP_TILES', short_name: 'OSM', long_name: 'OpenStreetMap', error_message: 'OpenStreetMap tiles unavailable', min_res: 1, max_res: 17, template: 'http://tile.openstreetmap.org/{Z}/{X}/{Y}.png', copyright: 'Map data from OpenStreetMap.org'});
    OSM_OSMARENDER_TILES = CustomTileBasedLayer({object_name: 'OSM_OSMARENDER_TILES', short_name: 'Osmarender', long_name: 'OSM Osmarender', error_message: 'OpenStreetMap tiles unavailable', min_res: 1, max_res: 17, template: 'http://tah.openstreetmap.org/Tiles/tile/{Z}/{X}/{Y}.png', copyright: 'Map data from OpenStreetMap.org'});
    OPENCYCLEMAP_TILES = CustomTileBasedLayer({object_name: 'OPENCYCLEMAP_TILES', short_name: 'OCM', long_name: 'OpenCycleMap topo', error_message: 'OpenCycleMap tiles unavailable', min_res: 1, max_res: 17, template: 'http://a.andy.sandbox.cloudmade.com/tiles/cycle/{Z}/{X}/{Y}.png', copyright: 'Map data from OpenCycleMap.org'});
    OPENSEAMAP_TILES = CustomTileBasedLayer({object_name: 'OPENSEAMAP_TILES', short_name: 'OpenSea', long_name: 'OpenSeaMap', error_message: 'OpenSeaMap tiles unavailable', min_res: 1, max_res: 17, template: ['http://tile.openstreetmap.org/{Z}/{X}/{Y}.png', 'http://tiles.openseamap.org/seamark/{Z}/{X}/{Y}.png'], copyright: 'Map data from OpenSeaMap.org'});
    YAHOO_MAP = CustomTileBasedLayer({object_name: 'YAHOO_MAP', short_name: 'Y. map', long_name: 'Yahoo map', error_message: 'Yahoo tiles unavailable', min_res: 1, max_res: 17, template: ['http://png.maps.yimg.com/png?t=m&v=4.1&s=256&x={X}&y={Y}&z={Z}'], tile_function: 'function(a,b){return "http://png.maps.yimg.com/png?t=m&v=4.1&s=256&x="+a.x+"&y="+(((1<<b)>>1)-1-a.y)+"&z="+(18-b);}', copyright: 'Map data from <a target="_blank" href="http://maps.yahoo.com/">Yahoo!</a>'});
    YAHOO_AERIAL = CustomTileBasedLayer({object_name: 'YAHOO_AERIAL', short_name: 'Y. aerial', long_name: 'Yahoo aerial', error_message: 'Yahoo tiles unavailable', min_res: 1, max_res: 20, template: ['http://aerial.maps.yimg.com/ximg?t=a&v=1.7&s=256&x={X}&y={Y}&z={Z}'], tile_function: 'function(a,b){return "http://aerial.maps.yimg.com/ximg?t=a&v=1.7&s=256&x="+a.x+"&y="+(((1<<b)>>1)-1-a.y)+"&z="+(18-b);}', copyright: 'Aerial imagery from <a target="_blank" href="http://maps.yahoo.com/">Yahoo!</a>'});
    YAHOO_AERIAL_BACKGROUND = CustomTileBasedLayer({object_name: 'YAHOO_AERIAL_BACKGROUND', short_name: 'Y. aerial', long_name: 'Yahoo aerial', error_message: 'Yahoo tiles unavailable', min_res: 1, max_res: 17, template: ['http://aerial.maps.yimg.com/ximg?t=a&v=1.7&s=256&x={X}&y={Y}&z={Z}'], tile_function: 'function(a,b){return "http://aerial.maps.yimg.com/ximg?t=a&v=1.7&s=256&x="+a.x+"&y="+(((1<<b)>>1)-1-a.y)+"&z="+(18-b);}', copyright: 'Aerial imagery from <a target="_blank" href="http://maps.yahoo.com/">Yahoo!</a>'});
    YAHOO_HYBRID = CustomTileBasedLayer({object_name: 'YAHOO_HYBRID', short_name: 'Y. aerial', long_name: 'Yahoo aerial', error_message: 'Yahoo tiles unavailable', min_res: 1, max_res: 17, template: ['http://png.maps.yimg.com/png?t=h&v=4.1&s=256&x={X}&y={Y}&z={Z}'], tile_function: 'function(a,b){return "http://png.maps.yimg.com/png?t=h&v=4.1&s=256&x="+a.x+"&y="+(((1<<b)>>1)-1-a.y)+"&z="+(18-b);}', copyright: 'Map and aerial from <a target="_blank" href="http://maps.yahoo.com/">Yahoo!</a>', background: 'YAHOO_AERIAL_BACKGROUND'});

    if (gv_options && gv_options['map_type_control'] && gv_options['map_type_control']['custom'] && gv_options['map_type_control']['custom'].length > 0) {
        gv_custom_background_maps = [];
        for (var i = 0; i < gv_options['map_type_control']['custom'].length; i++) {
            var custom = gv_options['map_type_control']['custom'][i];
            if (custom['url'] || custom['template']) {
                var object_name = (custom['id']) ? custom['id'] : 'CUSTOM_TILES_' + (i + 1);
                var url = (custom['url']) ? custom['url'] : custom['template'];
                var tile_size = (custom['tile_size']) ? custom['tile_size'] : 256;
                var tile_function = (custom['tile_function']) ? custom['tile_function'] : '';
                var min_res = (custom['min_res']) ? parseInt(custom['min_res']) : 0;
                var max_res = (custom['max_res']) ? parseInt(custom['max_res']) : 20;
                var menu_name = (custom['menu_name']) ? custom['menu_name'] : 'Custom ' + (i + 1);
                var description = (custom['description']) ? custom['description'] : '';
                var copyright = (custom['copyright']) ? custom['copyright'] : '';
                var error_message = (custom['error_message']) ? custom['error_message'] : 'Background map server unavailable';
                var background = '';
                var foreground = '';
                var url_text = url;
                var url_list = '';
                if (custom['background']) {
                    if (typeof(custom['background']) != 'object') {
                        custom['background'] = [custom['background']];
                    }
                    for (var j = 0; j < custom['background'].length; j++) {
                        background = background + "'" + custom['background'][j] + "',";
                    }
                    background = background.replace(/,$/, '');
                }
                if (custom['foreground']) {
                    if (typeof(custom['foreground']) != 'object') {
                        custom['foreground'] = [custom['foreground']];
                    }
                    for (var j = 0; j < custom['foreground'].length; j++) {
                        foreground = foreground + "'" + custom['foreground'][j] + "',";
                    }
                    foreground = foreground.replace(/,$/, '');
                }
                if (url) {
                    if (typeof(url) == 'string') {
                        url = [url];
                    }
                    for (var j = 0; j < url.length; j++) {
                        url_list = url_list + "'" + url[j].replace(/'/g, "\\'") + "',";
                    }
                    url_list = url_list.replace(/,$/, '');
                }
                var bg_opacity = (custom['bg_opacity']) ? parseFloat(custom['bg_opacity']) : null;
                var fg_opacity = (custom['fg_opacity']) ? parseFloat(custom['fg_opacity']) : null;
                var definition = '';
                if (custom['wms'] === false || custom['tile_based'] || url_list.match(/\{[XYZ]\}.*\{[XYZ]\}.*\{[XYZ]\}/)) {
                    definition = object_name + " = CustomTileBasedLayer({short_name:'" + menu_name.replace(/'/g, "\\'") + "',long_name:'" + menu_name.replace(/'/g, "\\'") + "',copyright:'" + copyright.replace(/'/g, "\\'") + "',tile_function:'" + tile_function.replace(/'/g, "\\'") + "',error_message:'" + error_message.replace(/'/g, "\\'") + "',min_res:" + min_res + ",max_res:" + max_res + ",template:[" + url_list + "],background:[" + background + "],foreground:[" + foreground + "],bg_opacity:" + bg_opacity + ",fg_opacity:" + fg_opacity + "});";
                } else {
                    definition = object_name + " = CustomWMSLayers([{name:'" + menu_name.replace(/'/g, "\\'") + "',copyright:'" + copyright.replace(/'/g, "\\'") + "',error_message:'" + error_message.replace(/'/g, "\\'") + "',minResolution:0,maxResolution:20,tile_size:" + parseInt(tile_size) + ",base_url:[" + url_list + "],background:[" + background + "],foreground:[" + foreground + "],bg_opacity:" + bg_opacity + ",fg_opacity:" + fg_opacity + "}]);";
                }
                eval(definition);
                eval(object_name + ".object_name = '" + object_name + "'");
                eval(object_name + ".menu_name = '" + menu_name.replace(/'/g, "\\'") + "'");
                eval(object_name + ".title = '" + description.replace(/'/g, "\\'") + "'");
                eval(object_name + ".bounds = [-180,-90,180,90]");
                eval(object_name + ".excluded = []");
                eval('gv_custom_background_maps.push(' + object_name + ')');
            }
        }
    }

}

function GV_Add_Custom_Layers(this_map) {
    for (var j = 0; j < gv_background_maps.length; j++) {
        this_map.addMapType(gv_background_maps[j]);
    }
    if (self.gv_custom_background_maps && gv_custom_background_maps.length > 0) {
        for (var i = 0; i < gv_custom_background_maps.length; i++) {
            this_map.addMapType(gv_custom_background_maps[i]);
        }
    }
}

/**************************************************
 * Custom map-type control:
 * more or less from Google's own documentation
 **************************************************/
function GV_MapTypeControl(options) {
    if (!self.gv_map_object && self.gv_options) {
        gv_map_object = eval(gv_options['map']);
    } // this really should have been done long ago, but with some OLD code, this may be the best opportunity
    this.control_style = (options && options['style'] != null) ? options['style'] : 'menu';
    this.filter_map_types = (options && options['filter'] != null) ? options['filter'] : false;
    this.excluded_map_types = (options && options['excluded'] != null) ? options['excluded'] : [];
    this.included_map_types = (options && options['included'] != null) ? options['included'] : [];
}
if (gv_api_version >= 2) {
    GV_MapTypeControl.prototype = new GControl();
    GV_MapTypeControl.prototype.getDefaultPosition = function() {
        return new GControlPosition(G_ANCHOR_TOP_RIGHT, new GSize(7, 7));
    }
    GV_MapTypeControl.prototype.initialize = function(this_map) {
        GV_Add_Custom_Layers(this_map);
        var map_types = [];
        map_types.push(
                {menu_name: 'Google map', type: 'G_NORMAL_MAP', title: 'Google street map', bounds: [-180, -90, 180, 90], excluded: []}
        , {menu_name: 'Google aerial', type: 'G_SATELLITE_MAP', title: 'Google aerial/satellite imagery', bounds: [-180, -90, 180, 90], excluded: []}
        , {menu_name: 'Google hybrid', type: 'G_HYBRID_MAP', title: 'Google "hybrid" map', bounds: [-180, -90, 180, 90], excluded: []}
        );
        if (self.G_PHYSICAL_MAP) {
            map_types.push({menu_name: 'Google terrain', type: 'G_PHYSICAL_MAP', title: 'Google terrain map', bounds: [-180, -90, 180, 90], excluded: []});
        }
        map_types.push(
                {menu_name: 'U.S./Can.: MyTopo', type: 'MYTOPO_TILES', title: 'Canadian topo tiles from MyTopo.com', bounds: [-169, 18, -52, 85], excluded: [], country: 'us,ca'}
        , {menu_name: 'USGS topo', type: 'USGS_TOPO_TILES', title: 'USGS topographic map', bounds: [-169, 18, -66, 72], excluded: [], country: 'us'}
        , {menu_name: 'USGS aerial', type: 'USGS_AERIAL_TILES', title: 'USGS aerial photos (black/white)', bounds: [-152, 17, -65, 65], excluded: [], country: 'us'}
        , {menu_name: 'USGS aerial+G.', type: 'USGS_AERIAL_HYBRID_TILES', title: 'USGS aerial photos (black/white) + Google street map', bounds: [-152, 17, -65, 65], excluded: [], country: 'us'}
        // ,{ menu_name:'U.S. Nexrad',type:'NEXRAD_TILES',title:'United States NEXRAD weather radar',bounds:[-152,17,-65,65],excluded:[-129,49.5,-66,72],country:'us' }
        , {menu_name: 'U.S. counties', type: 'US_COUNTY_TILES', title: 'United States county outlines', bounds: [-169, 18, -66, 72], excluded: [-129, 49.5, -66, 72], country: 'us'}
        // ,{ menu_name:'U.S. counties+sat.',type:'US_COUNTY_HYBRID_TILES',title:'United States county outlines + Google satellite',bounds:[-169,18,-66,72],excluded:[-129,49.5,-66,72],country:'us' }
        , {menu_name: 'Canada NRCan', type: 'NRCAN_TOPO_TILES', title: 'NRCan/Toporama maps with contour lines', bounds: [-141, 41.7, -52, 85], excluded: [-141, 41.7, -86, 48], country: 'ca'}
        , {menu_name: 'Can. NRCan+names', type: 'NRCAN_TOPO_NAMES_TILES', title: 'NRCan topo maps with feature names', bounds: [-141, 41.7, -52, 85], excluded: [-141, 41.7, -86, 48], country: 'ca'}
        , {menu_name: 'Can. NRCan + G.', type: 'NRCAN_TOPO_HYBRID_TILES', title: 'NRCan topo maps with Google street names', bounds: [-141, 41.7, -52, 85], excluded: [-141, 41.7, -86, 48], country: 'ca'}
        , {menu_name: 'Landsat 30m', type: 'LANDSAT_TILES', title: 'NASA Landsat 30-meter imagery', bounds: [-180, -90, 180, 90], excluded: []}
        // ,{ menu_name:'DEMIS physical',type:'DEMIS_PHYSICAL_TILES',title:'DEMIS physical map (no labels)',bounds:[-180,-90,180,90],excluded:[] }
        , {menu_name: 'Blue Marble', type: 'BLUEMARBLE_TILES', title: 'NASA "Visible Earth" image', bounds: [-180, -90, 180, 90], excluded: []}
        // ,{ menu_name:'Daily "Terra"',type:'DAILY_TERRA_TILES',title:'Daily imagery from "Terra" satellite',bounds:[-180,-90,180,90],excluded:[] }
        // ,{ menu_name:'Daily "Aqua"',type:'DAILY_AQUA_TILES',title:'Daily imagery from "Aqua" satellite',bounds:[-180,-90,180,90],excluded:[] }
        , {menu_name: 'Daily MODIS', type: 'DAILY_MODIS_TILES', title: 'Daily imagery from Nasa\'s MODIS satellites', bounds: [-180, -90, 180, 90], excluded: []}
        // ,{ menu_name:'SRTM elevation',type:'SRTM_COLOR_TILES',title:'SRTM elevation data, as color',bounds:[-180,-90,180,90],excluded:[] }
        , {menu_name: 'OpenStreetMap', type: 'OPENSTREETMAP_TILES', title: 'OpenStreetMap.org', bounds: [-180, -90, 180, 90], excluded: []}
        // ,{ menu_name:'OSM Osmarender',type:'OSM_OSMARENDER_TILES',title:'OpenStreetMap Osmarender',bounds:[-180,-90,180,90],excluded:[] }
        , {menu_name: 'OpenCycleMap topo', type: 'OPENCYCLEMAP_TILES', title: 'OpenCycleMap.org', bounds: [-180, -90, 180, 90], excluded: []}
        // ,{ menu_name:'OpenSeaMap',type:'OPENSEAMAP_TILES',title:'OpenSeaMap.org',bounds:[-180,-90,180,90],excluded:[] }
        , {menu_name: 'Yahoo map', type: 'YAHOO_MAP', title: 'Yahoo street map', bounds: [-180, -90, 180, 90], excluded: []}
        , {menu_name: 'Yahoo aerial', type: 'YAHOO_AERIAL', title: 'Yahoo aerial/satellite imagery', bounds: [-180, -90, 180, 90], excluded: []}
        , {menu_name: 'Yahoo hybrid', type: 'YAHOO_HYBRID', title: 'Yahoo "hybrid" map', bounds: [-180, -90, 180, 90], excluded: []}
        );
        if (self.G_SATELLITE_3D_MAP) {
            map_types.push({menu_name: 'Google Earth', type: 'G_SATELLITE_3D_MAP', title: 'Google Earth plugin', bounds: [-180, -90, 180, 90], excluded: []});
        }
        if (self.gv_custom_background_maps && gv_custom_background_maps.length > 0) {
            for (var j = 0; j < gv_custom_background_maps.length; j++) {
                map_types.push(
                        {menu_name: gv_custom_background_maps[j].menu_name, type: gv_custom_background_maps[j].object_name, title: gv_custom_background_maps[j].title, bounds: gv_custom_background_maps[j].bounds, excluded: gv_custom_background_maps[j].excluded}
                );
            }
        }
        var center_lat = this_map.getCenter().lat();
        var center_lng = this_map.getCenter().lng();

        var excluded_maps = [];
        var excluded_maps_count = 0;
        var included_maps = [];
        var included_maps_count = 0;
        var oor_maps = []; // out-of-range
        if (this.excluded_map_types) {
            for (var j = 0; j < this.excluded_map_types.length; j++) {
                excluded_maps[this.excluded_map_types[j]] = true;
                excluded_maps_count += 1;
            }
        }
        if (this.included_map_types) {
            for (var j = 0; j < this.included_map_types.length; j++) {
                included_maps[this.included_map_types[j]] = true;
                included_maps_count += 1;
            }
        }
        for (var j = 0; j < map_types.length; j++) {
            if (self.gv_options && gv_options['country']) {
                if (map_types[j]['country'] && map_types[j]['country'].indexOf(gv_options['country']) < 0) {
                    oor_maps[map_types[j]['type']] = true;
                }
            } else {
                if (!(center_lng >= map_types[j]['bounds'][0] && center_lat >= map_types[j]['bounds'][1] && center_lng <= map_types[j]['bounds'][2] && center_lat <= map_types[j]['bounds'][3]) || (center_lng >= map_types[j]['excluded'][0] && center_lat >= map_types[j]['excluded'][1] && center_lng <= map_types[j]['excluded'][2] && center_lat <= map_types[j]['excluded'][3])) {
                    oor_maps[map_types[j]['type']] = true;
                }
            }
        }
        if (this.control_style == 'menu') {
            var map_selector = document.createElement("select");
            map_selector.id = 'gv_map_selector';
            map_selector.title = "Scegli una mappa";
            map_selector.style.font = '10px Verdana';
            map_selector.style.backgroundColor = '#FFFFFF';
            for (var j = 0; j < map_types.length; j++) {
                var map_ok = (excluded_maps[map_types[j]['type']] || (included_maps_count > 0 && !included_maps[map_types[j]['type']]) || (this.filter_map_types && oor_maps[map_types[j]['type']])) ? false : true;
                if (map_ok) {
                    var opt = document.createElement("option");
                    opt.value = map_types[j]['type'];
                    var menu_name = map_types[j]['menu_name'];
                    if (self.gv_options && gv_options['map_type_control'] && gv_options['map_type_control']['custom_title'] && gv_options['map_type_control']['custom_title'][map_types[j]['type']]) {
                        menu_name = gv_options['map_type_control']['custom_title'][map_types[j]['type']];
                    }
                    opt.appendChild(document.createTextNode(menu_name));
                    map_selector.appendChild(opt);
                    if (this_map.getCurrentMapType() == eval(opt.value)) {
                        map_selector.selectedIndex = map_selector.length - 1;
                    }
                }
            }
            if (self.G_SATELLITE_3D_MAP && this_map.getCurrentMapType() == G_SATELLITE_3D_MAP) { // if the GE view was initially selected, the link needs to appear
                GV_Google_Earth_Exit_Link(true);
            }
            GEvent.addDomListener(map_selector, "change", function() {
                this_map.setMapType(eval(this.value));
                if (this.value == 'G_SATELLITE_3D_MAP') {
                    GV_Google_Earth_Exit_Link(true);
                } else {
                    GV_Google_Earth_Exit_Link(false);
                }
                // if (self.gv_maptypecontrol) {
                // 	this_map.removeControl(gv_maptypecontrol);
                // 	this_map.addControl(gv_maptypecontrol);
                // }
            });
            this_map.getContainer().appendChild(map_selector);
            return map_selector;
        } else { // 'list'
            var map_type_container = document.createElement("div");
            for (var j = 0; j < map_types.length; j++) {
                var map_ok = (excluded_maps[map_types[j]['type']] || (included_maps_count > 0 && !included_maps[map_types[j]['type']]) || (this.filter_map_types && oor_maps[map_types[j]['type']])) ? false : true;
                if (map_ok) {
                    var maplink = document.createElement("div");
                    maplink.className = 'gv_maptypelink';
                    if (self.gv_maptypecontrol && this_map.getCurrentMapType() == eval(map_types[j]['type'])) {
                        maplink.className = 'gv_maptypelink gv_maptypelink_selected';
                    }
                    maplink.title = map_types[j]['title'];
                    maplink.type = map_types[j]['type'];
                    map_type_container.appendChild(maplink);
                    var menu_name = map_types[j]['menu_name'];
                    if (self.gv_options && gv_options['map_type_control'] && gv_options['map_type_control']['custom_title'] && gv_options['map_type_control']['custom_title'][map_types[j]['type']]) {
                        menu_name = gv_options['map_type_control']['custom_title'][map_types[j]['type']];
                    }
                    maplink.appendChild(document.createTextNode(menu_name));
                    GEvent.addDomListener(maplink, "click", function() {
                        this_map.setMapType(eval(this.type));
                        if (self.gv_maptypecontrol) {
                            this_map.removeControl(gv_maptypecontrol);
                            this_map.addControl(gv_maptypecontrol);
                        }
                    });
                }
            }
            this_map.getContainer().appendChild(map_type_container);
            return map_type_container;
        }
    }
}
function GV_Google_Earth_Exit_Link(create) {
    var exit_id = 'gv_exit_google_earth';
    var menu_id = 'gv_map_selector';
    if (create) {
        if (!$(exit_id)) {
            var exit_div = document.createElement('div');
            exit_div.id = exit_id;
            exit_div.style.display = 'none';
            exit_div.innerHTML = '<table cellspacing="0" cellpadding="0" border="0"><tr><td><div style="background-color:#ffffff; padding:2px; font-family:Arial; font-size:10px; line-height:11px;"><a href="javascript:void(0)" onclick="gmap.setMapType(G_HYBRID_MAP); if ($(\'' + menu_id + '\')) { $(\'' + menu_id + '\').selectedIndex = 2; GV_Remove_Control(\'' + exit_id + '\'); }">return to normal view</a></div></td></tr></table>';
            gv_map_object.getContainer().appendChild(exit_div);
        }
        $(exit_id).style.display = 'block';
        gv_map_object.getContainer().parentNode.appendChild($(exit_id));
    } else { // remove it
        if ($(exit_id)) {
            GV_Remove_Control(exit_id);
        }
    }
}

/**************************************************
 * More custom map layer functions:
 * Adapted from Jef Poskanzer's Acme Mapper
 * (http://mapper.acme.com/)
 **************************************************/
function CustomWMSLayers(layers) {

    var tileLayers = [];
    for (var j = 0; j < layers.length; j++) {
        var mi = layers[j]; // mi stands for "map info"
        if (mi['background']) {
            if (typeof(mi['background']) == 'string') {
                mi['background'] = [mi['background']];
            } // force it into an array
            for (var j = 0; j < mi['background'].length; j++) {
                if (eval("self." + mi['background'][j]) != undefined) {
                    var bg_layers = eval(mi['background'][j] + '.getTileLayers()');
                    for (var i in bg_layers) {
                        if (mi['bg_opacity'] != undefined) {
                            bg_layers[i].getOpacity = function() {
                                return mi['bg_opacity'];
                            }
                        }
                        tileLayers.push(bg_layers[i]);
                    }
                }
            }
        }
        var urls = (typeof(mi['base_url']) == 'object') ? mi['base_url'] : [mi['base_url']]; // force it into an array
        for (u = 0; u < urls.length; u++) {
            var tileLayer = new GTileLayer(new GCopyrightCollection(mi['copyright']), mi['minResolution'], mi['maxResolution']);
            tileLayer.base_url = urls[u];
            tileLayer.tile_size = mi['tile_size'];
            tileLayer.getTileUrl = WMSGetTileUrl;
            tileLayer.getCopyright = function() {
                return {prefix: '', copyrightTexts: [mi['copyright']]};
            };
            if (mi['opacity'] != undefined) {
                tileLayer.getOpacity = function() {
                    return mi['opacity'];
                }
            }
            tileLayers.push(tileLayer);
        }
        if (mi['foreground']) {
            if (typeof(mi['foreground']) == 'string') {
                mi['foreground'] = [mi['foreground']];
            } // force it into an array
            for (var j = 0; j < mi['foreground'].length; j++) {
                if (eval("self." + mi['foreground'][j]) != undefined) {
                    var fg_layers = eval(mi['foreground'][j] + '.getTileLayers()');
                    for (var i in fg_layers) {
                        if (mi['fg_opacity'] != undefined) {
                            fg_layers[i].getOpacity = function() {
                                return mi['fg_opacity'];
                            }
                        }
                        if (mi['foreground'] != 'G_HYBRID_MAP' || (mi['foreground'] == 'G_HYBRID_MAP' && i == (fg_layers.length - 1))) { // if the foreground is Google hybrid, only use the last (transparent) layer
                            tileLayers.push(fg_layers[i]);
                        }
                    }
                }
            }
        }
    }
    var bg_map = new GMapType(tileLayers, G_SATELLITE_MAP.getProjection(), mi['name'], {errorMessage: mi['error_message'], tileSize: mi['tile_size']});
    gv_background_maps.push(bg_map);
    return bg_map;
}
function WMSGetTileUrl(tile, zoom) {
    var lat_first = (this.base_url.match(/version=1.[3-9]/i)) ? true : false;
    var southWestPixel = new GPoint(tile.x * this.tile_size, (tile.y + 1) * this.tile_size);
    var northEastPixel = new GPoint((tile.x + 1) * this.tile_size, tile.y * this.tile_size);
    var southWestCoords = G_NORMAL_MAP.getProjection().fromPixelToLatLng(southWestPixel, zoom);
    var northEastCoords = G_NORMAL_MAP.getProjection().fromPixelToLatLng(northEastPixel, zoom);
    var bbox = (lat_first) ? southWestCoords.lat() + ',' + southWestCoords.lng() + ',' + northEastCoords.lat() + ',' + northEastCoords.lng() : southWestCoords.lng() + ',' + southWestCoords.lat() + ',' + northEastCoords.lng() + ',' + northEastCoords.lat();
    var ts = (this.base_url.indexOf('onearth.jpl.nasa.gov') > -1 && this.tile_size == 256) ? 257 : this.tile_size;
    return this.base_url + '&bbox=' + bbox + '&width=' + ts + '&height=' + ts;
}

/**************************************************
 Custom tile-based layer code adapted from MyTopo.com
 **************************************************/
function CustomTileBasedLayer(opts) {
    var short_name = (opts['short_name']) ? opts['short_name'] : '';
    var long_name = (opts['long_name']) ? opts['long_name'] : '';
    var min_res = (opts['min_res']) ? opts['min_res'] : 1;
    var max_res = (opts['max_res']) ? opts['max_res'] : 18;
    var template = (typeof(opts['template']) == 'object') ? opts['template'] : [opts['template']]; // force it into an array
    var tile_function = (opts['tile_function']) ? opts['tile_function'] : null;
    var error_message = (opts['error_message']) ? opts['error_message'] : '';
    var copyright = (opts['copyright']) ? opts['copyright'] : '';
    if (!template) {
        return null;
    }
    var tileLayers = [];
    if (opts['background']) {
        if (typeof(opts['background']) == 'string') {
            opts['background'] = [opts['background']];
        }
        for (var j = 0; j < opts['background'].length; j++) {
            if (eval('self.' + opts['background'][j])) {
                var bg_layers = eval(opts['background'][j] + '.getTileLayers()');
                for (var i in bg_layers) {
                    if (opts['bg_opacity'] != undefined) {
                        bg_layers[i].getOpacity = function() {
                            return opts['bg_opacity'];
                        }
                    }
                    tileLayers.push(bg_layers[i]);
                }
            }
        }
    }
    for (j = 0; j < template.length; j++) {
        var layer1 = new GTileLayer(
                new GCopyrightCollection(copyright), min_res, max_res,
                {isPng: true, tileUrlTemplate: template[j]}
        );
        layer1.getCopyright = function() {
            return copyright;
        };
        layer1.layer = long_name;
        if (tile_function) {
            eval('layer1.getTileUrl=' + tile_function);
        }
        tileLayers.push(layer1);
    }
    if (opts['foreground']) {
        if (typeof(opts['foreground']) == 'string') {
            opts['foreground'] = [opts['foreground']];
        }
        for (var j = 0; j < opts['foreground'].length; j++) {
            if (eval('self.' + opts['foreground'][j])) {
                var fg_layers = eval(opts['foreground'][j] + '.getTileLayers()');
                for (var i in fg_layers) {
                    if (opts['fg_opacity'] != undefined) {
                        fg_layers[i].getOpacity = function() {
                            return opts['fg_opacity'];
                        }
                    }
                    if (opts['foreground'] != 'G_HYBRID_MAP' || (opts['foreground'] == 'G_HYBRID_MAP' && i == (fg_layers.length - 1))) { // if the foreground is Google hybrid, only use the last (transparent) layer
                        tileLayers.push(fg_layers[i]);
                    }
                }
            }
        }
    }
    var bg_map = new GMapType(tileLayers, G_SATELLITE_MAP.getProjection(), short_name, {errorMessage: error_message, tileSize: 256});
    gv_background_maps.push(bg_map);
    return bg_map;
}


function GV_MapOpacityControl(op) {
    this.initial_opacity = (op != null) ? parseFloat(op) : 100;
}
if (google_api_version > 0) {
    GV_MapOpacityControl.prototype = new GControl();
    GV_MapOpacityControl.prototype.getDefaultPosition = function() {
        var from_right = 6;
        if ($('gv_map_selector')) {
            from_right = $('gv_map_selector').offsetWidth + 7 + 4;
        } else if (self.gv_maptypecontrol && gv_maptypecontrol) { // google default map switcher?
            from_right = 200;
        }
        return new GControlPosition(G_ANCHOR_TOP_RIGHT, new GSize(from_right, 7));
    }
    GV_MapOpacityControl.prototype.initialize = function(map) {
        var opacity_selector = document.createElement("select");
        opacity_selector.id = 'gv_opacity_selector';
        opacity_selector.title = "Seleziona l'opacit� della mappa";
        opacity_selector.style.font = '10px Verdana';
        opacity_selector.style.backgroundColor = '#FFFFFF';
        var opt = document.createElement("option");
        opt.value = '1';
        opt.appendChild(document.createTextNode('opacity'));
        opacity_selector.appendChild(opt);
        for (var j = 10; j >= 0; j--) {
            var opt = document.createElement("option");
            opt.value = j / 10;
            opt.appendChild(document.createTextNode(j * 10 + '%'));
            opacity_selector.appendChild(opt);
        }
        GEvent.addDomListener(opacity_selector, "change", function() {
            GV_Background_Opacity(map, this.value);
        });
        map.getContainer().appendChild(opacity_selector);
        GV_Background_Opacity(map, this.initial_opacity);
        return opacity_selector;
    }
}

function GV_Tracklist_Tooltip_Show(item_div_name, map_div_name) {
    var map_div, legend_div, item_div, tooltip_div;
    if (!map_div_name) {
        map_div_name = 'gmap_div';
    }
    if ($(item_div_name)) {
        item_div = $(item_div_name);
    } else {
        return false;
    }
    if ($(map_div_name)) {
        map_div = $(map_div_name);
    } else {
        return false;
    }
    if ($('gv_legend_tooltip')) {
        tooltip_div = $('gv_legend_tooltip');
    }
    else if ($('gv_tracklist_tooltip')) {
        tooltip_div = $('gv_tracklist_tooltip');
    }
    else {
        return false;
    }
    if (item_div.title && !item_div.description) {
        item_div.description = item_div.title;
        item_div.title = '';
    }
    if (item_div && tooltip_div && item_div.description) {
        tooltip_div.innerHTML = item_div.description;
        tooltip_div.style.position = 'absolute';
        tooltip_div.style.zIndex = -99999;
        tooltip_div.style.display = 'block';
        var map_pos = findPos(map_div);
        var item_pos = findPos(item_div);
        var item_height = item_div.offsetHeight || 12;
        var map_right = map_pos[0] + parseInt(map_div.offsetWidth);
        var tooltip_padding = parseInt(tooltip_div.style.padding);
        var tooltip_border = parseInt(tooltip_div.style.borderWidth);
        var tooltip_width = parseInt(tooltip_div.style.width) || tooltip_div.clientWidth || 200;
        var max_right = parseInt(map_right - tooltip_width - tooltip_padding * 2 - tooltip_border * 2);
        tooltip_div.style.left = (item_pos[0] > max_right) ? max_right + 'px' : item_pos[0] + 'px';
        tooltip_div.style.top = parseInt(item_pos[1] + item_height + 3) + 'px';
        tooltip_div.style.zIndex = 99999;
        tooltip_div.style.visibility = 'visible';
    }
}

function GV_Tracklist_Tooltip_Hide(item_div_name) {
    var item_div, tooltip_div;
    if ($('gv_legend_tooltip')) {
        tooltip_div = $('gv_legend_tooltip');
    }
    else if ($('gv_tracklist_tooltip')) {
        tooltip_div = $('gv_tracklist_tooltip');
    }
    else {
        return false;
    }
    if ($(item_div_name)) {
        item_div = $(item_div_name);
    }
    if (tooltip_div) {
        tooltip_div.innerHTML = '';
        tooltip_div.style.visibility = 'hidden';
        tooltip_div.style.display = 'none';
        tooltip_div.style.top = '-2000px';
        // window.setTimeout("GV_Tracklist_Tooltip_Hide_Delayed('"+tooltip_div.id+"')",3000);
    }
}

function GV_Tracklist_Tooltip_Hide_Delayed(tooltip_div_name) {
    var tooltip_div = $(tooltip_div_name);
    if (tooltip_div) {
        tooltip_div.innerHTML = '';
        tooltip_div.style.visibility = 'hidden';
        tooltip_div.style.display = 'none';
        tooltip_div.style.top = '-2000px';
    }
}

function findPos(obj) {
    var left = 0;
    var top = 0;
    if (obj.offsetParent) {
        left = obj.offsetLeft;
        top = obj.offsetTop;
        while (obj = obj.offsetParent) {
            left += obj.offsetLeft;
            top += obj.offsetTop;
        }
    }
    return [left, top];
}


// These are here only for backwards compatibilty:
function GPSV_Waypoint(lon, lat, name, desc, url, color, style, width, label_id) {
    GV_Marker(gmap, lat, lon, name, desc, url, color, style, width, label_id);
}
function GPSV_Toggle_Track_And_Label(id, color) {
    GV_Toggle_Track_And_Label(id, color);
} // for backwards compatibility
function GPSV_Toggle_Opacity(overlay_array) {
    GV_Toggle_Overlays(overlay_array);
} // for backwards compatibility
function GPSV_Toggle_Label_Opacity(label, original_color) {
    GV_Toggle_Label_Opacity(label, original_color);
} // for backwards compatibility
function GPSV_MapTypeControl() {
}
if (google_api_version >= 2) {
    GPSV_MapTypeControl.prototype = GV_MapTypeControl.prototype;
}
function GV_Legend_Tooltip_Show(item_div_name, map_div_name) {
    GV_Tracklist_Tooltip_Show(item_div_name, map_div_name);
}
function GV_Legend_Tooltip_Hide(item_div_name) {
    GV_Tracklist_Tooltip_Hide(item_div_name);
}
function GV_Legend_Tooltip_Hide_Delayed(tooltip_div_name) {
    GV_Tracklist_Tooltip_Hide_Delayed(tooltip_div_name);
}



function GV_Load_Markers_From_XML(opts) {
    var map_name = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    var map = eval(map_name);
    var array_name = (opts['array']) ? opts['array'] : 'wpts';
    var marker_array = eval(array_name);
    var filename = (opts['xml']) ? opts['xml'] : 'markers.xml';
    if (opts['prevent_caching']) {
        var query_punctuation = (filename.indexOf('?') > -1) ? '&' : '?';
        var timestamp = new Date();
        filename = filename + query_punctuation + 'gv_nocache=' + timestamp.valueOf();
    }
    GDownloadUrl(filename, function(data, responseCode) {
        var xml = GXml.parse(data);
        var markers = xml.documentElement.getElementsByTagName(opts['tag'] || 'marker');
        for (var i = 0; i < markers.length; i++) {
            var m = new Array;
            m['lat'] = m['lon'] = null;
            if (markers[i].getAttribute('latitude')) {
                m['lat'] = parseFloat(markers[i].getAttribute('latitude'));
            }
            else if (markers[i].getAttribute('lat')) {
                m['lat'] = parseFloat(markers[i].getAttribute('lat'));
            }
            var lon = null;
            if (markers[i].getAttribute('longitude')) {
                m['lon'] = parseFloat(markers[i].getAttribute('longitude'));
            }
            else if (markers[i].getAttribute('lon')) {
                m['lon'] = parseFloat(markers[i].getAttribute('lon'));
            }
            else if (markers[i].getAttribute('lng')) {
                m['lon'] = parseFloat(markers[i].getAttribute('lng'));
            }
            else if (markers[i].getAttribute('long')) {
                m['lon'] = parseFloat(markers[i].getAttribute('long'));
            }
            if (m['lat'] && m['lon']) {
                m['name'] = m['desc'] = m['color'] = m['icon'] = '';
                if (markers[i].getAttribute('name')) {
                    m['name'] = markers[i].getAttribute('name');
                }
                if (markers[i].getAttribute('desc')) {
                    m['desc'] = markers[i].getAttribute('desc');
                }
                if (markers[i].getAttribute('color')) {
                    m['color'] = markers[i].getAttribute('color');
                }
                if (markers[i].getAttribute('icon')) {
                    m['icon'] = markers[i].getAttribute('icon');
                }
                else if (markers[i].getAttribute('sym')) {
                    m['icon'] = markers[i].getAttribute('sym');
                }
                else if (markers[i].getAttribute('symbol')) {
                    m['icon'] = markers[i].getAttribute('symbol');
                }
                if (markers[i].getAttribute('icon_size') && markers[i].getAttribute('icon_size').match(/([0-9]+)[^0-9]+([0-9]+)/)) {
                    var parts = markers[i].getAttribute('icon_size').match(/([0-9]+)[^0-9]+([0-9]+)/);
                    m['icon_size'] = [parts[1], parts[2]];
                } else if (markers[i].getAttribute('width') && markers[i].getAttribute('height')) {
                    m['icon_size'] = [markers[i].getAttribute('width'), markers[i].getAttribute('height')];
                }
                if (markers[i].getAttribute('scale')) {
                    m['scale'] = markers[i].getAttribute('scale');
                }
                marker_array.push(GV_Marker(map, {lat: m['lat'], lon: m['lon'], name: m['name'], desc: m['desc'], color: m['color'], icon: m['icon'], scale: m['scale'], icon_size: m['icon_size']}));
            }
        }

        // This needs to be done here because otherwise, due to "asynchronicity" or something, the map might get finished before this subroutine completes
        window.setTimeout('GV_Process_Markers(' + map_name + ',' + array_name + ')', 200);

    });
}


function GV_Reload_Markers_From_All_Files() {
    var map_name = (self.gv_options && gv_options['map']) ? gv_options['map'] : 'gmap';
    var map = eval('self.' + map_name);
    var marker_array_name = (self.gv_options && gv_options['marker_array']) ? gv_options['marker_array'] : 'wpts';
    var marker_array = eval('self.' + marker_array_name);
    for (var i = 0; i < marker_array.length; i++) {
        if (marker_array[i].dynamic > 0) {
            map.removeOverlay(marker_array[i]);
            if (marker_array[i].label_object) {
                map.removeOverlay(marker_array[i].label_object);
            }
        }
    }
    marker_array.length = gv_static_marker_count;

    for (var i = 0; i < gv_options['dynamic_data'].length; i++) {
        gv_options['dynamic_data'][i]['processed'] = false;
    }
    gv_dynamic_file_index = -1;
    gv_reloading_data = true;
    if (gv_options['dynamic_data'] && gv_options['dynamic_data'][gv_dynamic_file_index + 1] && (gv_options['dynamic_data'][gv_dynamic_file_index + 1]['url'] || gv_options['dynamic_data'][gv_dynamic_file_index + 1]['google_spreadsheet']) && !gv_options['dynamic_data'][gv_dynamic_file_index + 1]['processed']) {
        gv_dynamic_file_index += 1;
        GV_Load_Markers_From_File(); // this will call GV_Finish_Map
        return;
    }
}

function GV_Load_Markers_From_File(file_index) {
    if (file_index != null && (gv_dynamic_file_index == null || gv_dynamic_file_index < 0)) {
        gv_dynamic_file_index = file_index;
    }
    if (!self.gv_options || !gv_options['dynamic_data'] || !gv_options['dynamic_data'][gv_dynamic_file_index]) {
        return false;
    }
    var map_name = (self.gv_options && gv_options['map']) ? gv_options['map'] : 'gmap';
    var map = eval('self.' + map_name);
    var marker_array_name = (self.gv_options && gv_options['marker_array']) ? gv_options['marker_array'] : 'wpts';
    var marker_array = (eval('self.' + marker_array_name)) ? eval(marker_array_name) : null;
    if (!marker_array) {
        eval(marker_array_name + ' = []');
    }
    var opts = gv_options['dynamic_data'][gv_dynamic_file_index];
    var google_spreadsheet = false;
    var google_kml = false;
    // Some options for special cases:
    if (opts['google_spreadsheet'] && !opts['url']) {
        opts['url'] = opts['google_spreadsheet'];
        google_spreadsheet = true;
    }
    opts['url'] = opts['url'].replace(/^\s*<?(.*)>?\s*$/, '$1'); // remove white space and possible brackets
    if (opts['url'].match(/^http:\/\/maps\.google\./)) { // Google directions, My Maps, etc.
        var query_punctuation = (opts['url'].indexOf('?') > -1) ? '&' : '?';
        opts['url'] = opts['url'] + query_punctuation + 'output=kml';
        if (opts['url'].match(/&ll=[0-9\.\-]+,[0-9\.\-]+/) && opts['url'].match(/&z=[0-9]+/)) {
            var center_lat = parseFloat(opts['url'].replace(/^.*&ll=([0-9\.\-]+),([0-9\.\-]+).*$/, '$1'));
            var center_lon = parseFloat(opts['url'].replace(/^.*&ll=([0-9\.\-]+),([0-9\.\-]+).*$/, '$2'));
            var zoom = parseInt(opts['url'].replace(/^.*&z=([0-9]+).*$/, '$1'));
            // map.setCenter(new GLatLng(center_lat,center_lon),zoom);
            // opts['autozoom'] = false;
        }
        google_kml = true;
    } else if (google_spreadsheet || (opts['url'] && opts['url'].match(/(docs|spreadsheets?)\.google\.com/))) { // Google spreadsheet
        opts['root_tag'] = (opts['root_tag']) ? opts['root_tag'] : 'feed';
        opts['marker_tag'] = (opts['marker_tag']) ? opts['marker_tag'] : 'entry';
        opts['tag_prefix'] = (opts['tag_prefix']) ? opts['tag_prefix'] : 'gsx$';
        opts['content_tag'] = (opts['content_tag']) ? opts['content_tag'] : '$t';
        opts['tagnames_stripped'] = (opts['tagnames_stripped'] === false) ? false : true;
        opts['prevent_caching'] = false; // it mucks up Google Docs URLs
        google_spreadsheet = true;
    } else if (opts['url'].match(/findmespot\.com\/messageService/i)) { // SPOT feeds
        opts['root_tag'] = 'messageList';
        opts['marker_tag'] = (typeof(opts['marker_tag']) != 'undefined' && opts['marker_tag'] != '') ? opts['marker_tag'] : 'message';
        opts['track_tag'] = (typeof(opts['track_tag']) != 'undefined' && opts['track_tag'] != '') ? opts['track_tag'] : 'none';
        opts['track_segment_tag'] = (typeof(opts['track_segment_tag']) != 'undefined' && opts['track_segment_tag'] != '') ? opts['track_segment_tag'] : 'none';
        opts['track_point_tag'] = (typeof(opts['track_point_tag']) != 'undefined' && opts['track_point_tag'] != '') ? opts['track_point_tag'] : 'message';
        opts['tag_prefix'] = '';
        opts['content_tag'] = '';
        opts['tagnames_stripped'] = false;
        if (opts['track_options']) {
            opts['track_options']['name'] = 'SPOT positions';
        }
        if (opts['synthesize_fields'] && !opts['synthesize_fields']['name']) {
            opts['synthesize_fields']['name'] = '{timestamp}';
        }
        // opts['time_stamp'] = (typeof(opts['time_stamp']) != 'undefined' && opts['time_stamp'] != '') ? opts['time_stamp'] : 'timestamp';
    }
    gv_options['dynamic_data'][gv_dynamic_file_index] = opts;

    if (gv_marker_list_exists) {
        if (gv_dynamic_file_index == 0) {
            gv_marker_list_div.innerHTML = 'Loading markers...';
            gv_marker_list_html = '';
            gv_marker_list_folders = [];
        }
        if (gv_static_marker_count && !self.gv_static_markers_loaded) { // problem: this loads ALL static markers even if "filter:true" is enabled
            for (var s = 0; s < marker_array.length; s++) {
                var m = marker_array[s];
                if (typeof(m.list_html) != 'undefined') {
                    if (m.folder) {
                        gv_marker_list_folders[m.folder] = (gv_marker_list_folders[m.folder]) ? gv_marker_list_folders[m.folder] + m.list_html : m.list_html;
                    }
                    else {
                        gv_marker_list_html += m.list_html;
                    }
                }
            }
            gv_static_markers_loaded = true;
        }
    }

    if (google_spreadsheet || opts['url'].match(/json|\.js\b|-js\b/i)) {
        GV_Load_Markers_From_JSON(opts['url']);
    } else if (!google_spreadsheet && (google_kml || opts['url'].match(/\.(xml|kml|kmz|gpx)$/i) || opts['url'].match(/=(xml|kml|kmz|gpx)\b/i))) {
        GV_Load_Markers_From_XML_File(opts['url']);
    } else {
        GV_Load_Markers_From_XML_File(opts['url']);
    }

}

function GV_Reload_Markers_From_File(index, et_al) {
    if (!index) {
        index = 0;
    }
    if (!self.gv_options || !gv_options['dynamic_data'] || !gv_options['dynamic_data'][index]['url']) {
        return false;
    }
    var map_name = (self.gv_options && gv_options['map']) ? gv_options['map'] : 'gmap';
    var map = eval('self.' + map_name);
    var marker_array_name = (self.gv_options && gv_options['marker_array']) ? gv_options['marker_array'] : 'wpts';
    var marker_array = eval('self.' + marker_array_name);

    if (self.gv_options && gv_options['marker_filter_options'] && !gv_options['dynamic_data'][index]['reload_on_request']) {
        gv_options['marker_filter_options']['filter'] = false;
        gv_filter_waypoints = false;
    }

    var current_bounds = map.getBounds();
    var current_center = map.getCenter();
    var current_zoom = map.getZoom();
    var lat_center = current_center.lat().toFixed(7);
    var lon_center = current_center.lng().toFixed(7);
    var SW = current_bounds.getSouthWest();
    var NE = current_bounds.getNorthEast();

    // This whole next block is simply for the purpose of seeing if the map moved enough to bother reloading the points
    var moved_enough = true;
    if (self.gv_dynamic_data_last_zoom && (current_zoom != gv_dynamic_data_last_zoom)) {
        moved_enough = true; // regardless of how far they moved, reload if they change the zoom level
    } else if (self.gv_dynamic_data_last_center && gv_options['dynamic_data'][index]['movement_threshold']) {
        var width_in_meters = current_center.distanceFrom(new GLatLng(current_center.lat(), current_bounds.getNorthEast().lng())) * 2;
        var moved_in_meters = current_center.distanceFrom(gv_dynamic_data_last_center);
        var fraction_moved = moved_in_meters / width_in_meters;
        var pixels_moved = map.getContainer().clientWidth * fraction_moved;
        if (moved_in_meters == 0) { // if there was NO motion, then it was a manual reload... right?
            moved_enough = true;
        } else if (pixels_moved < parseFloat(gv_options['dynamic_data'][index]['movement_threshold'])) {
            moved_enough = false;
        }
    }
    gv_reloading_data = true;

    if (et_al) { // do all files with an index higher than this one too
        for (var i = index; i < gv_options['dynamic_data'].length; i++) {
            gv_options['dynamic_data'][i]['processed'] = false;
        }
    }

    if (moved_enough || gv_options['dynamic_data'][index]['reload_on_request']) {
        for (var i = 0; i < marker_array.length; i++) {
            if (marker_array[i].dynamic > 0) {
                if ((marker_array[i].dynamic - 1) == index || (et_al && (marker_array[i].dynamic - 1) > index)) {
                    map.removeOverlay(marker_array[i]);
                    if (marker_array[i].label_object) {
                        map.removeOverlay(marker_array[i].label_object);
                    }
                }
            }
        }
        gv_marker_count = 0;
        marker_array.length = gv_static_marker_count; // this is important!
        gv_dynamic_file_index = index; // so is this!
        GV_Load_Markers_From_File(); // it will never return from this function in a timely manner, which is a problem if you ever want to load two files
        gv_dynamic_data_last_center = map.getCenter();
        gv_dynamic_data_last_zoom = map.getZoom();
    }
}

function GV_Load_Markers_From_JSON(url) {
    var key = '';
    var sheet_id = 'default';
    var full_url;
    if (url.indexOf('http') < 1 && url.match(/(spreadsheets|docs)\.google\.\w+\//i)) {
        if (url.indexOf('key=') > -1) {
            key = url.replace(/^.*key=([A-Za-z0-9\._-]+).*/, '$1');
        } else if (url.indexOf('/feeds/') > -1) {
            key = url.replace(/^.*\/feeds\/\w+\/([A-Za-z0-9\._-]+).*/, '$1');
            sheet_id = url.replace(/^.*\/feeds\/\w+\/[A-Za-z0-9\._-]+\/(\w+).*/, '$1');
        }
    } else if (url.match(/(^http.*google\..*latitude\/apps\/badge|geojson)/i)) {
        // Google Mobiles's "Latitude" tracking system, which is known to have no ability to specify a callback function
        var proxy_program = 'http://maps.gpsvisualizer.com/google_maps/json_callback.cgi?callback=GV_Load_Markers_From_Data_Object&url=';
        full_url = proxy_program + uri_escape(url);
    } else if (url.match(/^http|^\/|\.js\b|json\b/)) {
        // non-google.com JSON URLs
        var query_punctuation = (url.indexOf('?') > -1) ? '&' : '?';
        full_url = url + query_punctuation + 'callback=GV_Load_Markers_From_Data_Object';
    } else { // it's maybe just a Google Docs key, not a URL at all (doesn't contain 'http')
        key = url;
    }
    if (key) {
        full_url = 'http://spreadsheets.google.com/feeds/list/' + key + '/' + sheet_id + '/public/values?alt=json-in-script&callback=GV_Load_Markers_From_Data_Object';
    }
    if (!full_url) {
        return false;
    }
    var clean = (gv_options['dynamic_data'][gv_dynamic_file_index]['prevent_caching'] == false) ? true : false;
    gv_json_script = new JSONscriptRequest(full_url, {clean: clean});
    gv_json_script.buildScriptTag(); // Build the dynamic script tag
    gv_json_script.addScriptTag(); // Add the script tag to the page
    /*
     // This next bit has to be done to ensure that GV_Finish_Map is indeed executed; if the JSON script was invalid, GV_Finish_Map won't happen because it's just a callback.
     // PROBLEM: this might actually run BEFORE the points get processed, giving an erroneous "it didn't work" message.
     window.setTimeout('GV_Check_For_Unsuccessful_JSON("'+key+'")',1000);
     */
}
function GV_Check_For_Unsuccessful_JSON(key) {
    gv_json_script2 = new JSONscriptRequest(''); // no URL; it's a custom JSON on-the-fly function
    var script = "if(gv_options['dynamic_data']&&gv_options['dynamic_data'][gv_dynamic_file_index]&&!gv_options['dynamic_data'][gv_dynamic_file_index]['processed']){";
    script += "gv_options['dynamic_data'][gv_dynamic_file_index]['processed']=true;";
    script += "if(gv_options['dynamic_data'][gv_dynamic_file_index]['url'].match(/spreadsheets\.google\./)){";
    script += "a" + "lert('This map was supposed to load data from a Google Docs spreadsheet (key: " + key + "), but the operation may have been unsuccessful. Please verify that the spreadsheet is \\'published.\\'');";
    script += "}";
    script += "GV_Finish_Map(gv_options);";
    script += "}"
    gv_json_script2.buildScriptTag_custom(script);
    gv_json_script2.addScriptTag(); // Add the script tag to the page
}

function GV_Load_Markers_From_XML_File(url) {
    var local_file = false;
    if (url.indexOf('http') == 0) {
        var first_slash_position = window.location.toString().substring(9).indexOf('/') + 9;
        var server = window.location.toString().substring(0, first_slash_position);
        if (url.indexOf(server) == 0 && !url.match(/csv$/i)) {
            local_file = true; // we can go ahead and grab files if they're on the same server
        } else {
            local_file = false;
            // Because JavaScript does not allow retrieving non-JS files from other servers,
            // this will have to be done with a XML-to-JSON proxy program on maps.gpsvisualizer.com
            var proxy_program;
            if (url.match(/(csv$|NavApiCSV)/i)) {
                proxy_program = 'http://maps.gpsvisualizer.com/google_maps/csv-json.php?url=';
            }
            else {
                proxy_program = 'http://maps.gpsvisualizer.com/google_maps/xml-json.cgi?url=';
            }
            GV_Load_Markers_From_JSON(proxy_program + uri_escape(url))
            return;
        }
    } else {
        local_file = true; // it didn't start with "http", so hopefully this local URL exists on the server
    }
    if (local_file) { // reload-on-move database queries might very well work with NON-local files via the XML-to-JSON proxy, but I'm not going to allow it!
        if (gv_options['dynamic_data'] && (gv_options['dynamic_data'][gv_dynamic_file_index]['reload_on_move'] || gv_options['dynamic_data'][gv_dynamic_file_index]['reload_on_request'])) {
            var map_name = (self.gv_options && gv_options['map']) ? gv_options['map'] : 'gmap';
            var map = eval('self.' + map_name);
            var SW = map.getBounds().getSouthWest();
            var NE = map.getBounds().getNorthEast();
            var query_punctuation = (url.indexOf('?') > -1) ? '&' : '?';
            var url = url + query_punctuation + 'lat_min=' + SW.lat().toFixed(7) + '&lat_max=' + NE.lat().toFixed(7) + '&lon_min=' + SW.lng().toFixed(7) + '&lon_max=' + NE.lng().toFixed(7) + '&lat_center=' + map.getCenter().lat() + '&lon_center=' + map.getCenter().lng() + '&zoom=' + map.getZoom();
            url += (parseFloat(gv_options['dynamic_data'][gv_dynamic_file_index]['limit']) > 0) ? '&limit=' + parseFloat(gv_options['dynamic_data'][gv_dynamic_file_index]['limit']) : '';
            url += (typeof(gv_options['dynamic_data'][gv_dynamic_file_index]['sort']) != 'undefined' && gv_options['dynamic_data'][gv_dynamic_file_index]['sort'] != '') ? '&sort=' + gv_options['dynamic_data'][gv_dynamic_file_index]['sort'] : '';
            url += (typeof(gv_options['dynamic_data'][gv_dynamic_file_index]['sort_numeric']) != 'undefined') ? '&sort_numeric=' + (gv_options['dynamic_data'][gv_dynamic_file_index]['sort_numeric'] ? 1 : 0) : '';
            url += (typeof(gv_options['dynamic_data'][gv_dynamic_file_index]['output_sort']) != 'undefined' && gv_options['dynamic_data'][gv_dynamic_file_index]['output_sort'] != '') ? '&output_sort=' + gv_options['dynamic_data'][gv_dynamic_file_index]['output_sort'] : '';
            url += (typeof(gv_options['dynamic_data'][gv_dynamic_file_index]['output_sort_numeric']) != 'undefined') ? '&output_sort_numeric=' + (gv_options['dynamic_data'][gv_dynamic_file_index]['output_sort_numeric'] ? 1 : 0) : '';
        }
        var prevent_caching = (gv_options['dynamic_data'] && gv_options['dynamic_data'][gv_dynamic_file_index]['prevent_caching'] == false) ? false : true;
        if (prevent_caching) {
            var query_punctuation = (url.indexOf('?') > -1) ? '&' : '?';
            var timestamp = new Date();
            url = url + query_punctuation + 'gv_nocache=' + timestamp.valueOf();
        }
        getURL(url, null, GV_Load_Markers_From_XML_File_callback);
        return;
    }
}
function GV_Load_Markers_From_XML_File_callback(text) {
    var xml_data = parseXML(text);
    var json_data = xml2json(xml_data, '', '');
    eval('var data_from_xml_file = ' + json_data);
    GV_Load_Markers_From_Data_Object(data_from_xml_file);
}

function GV_Load_Markers_From_Data_Object(data) {
    if (!gv_options['dynamic_data'][gv_dynamic_file_index]) {
        return;
    }
    var opts = gv_options['dynamic_data'][gv_dynamic_file_index];
    var root_tag, track_tag, track_segment_tag, track_point_tag, marker_tag, tag_prefix, prefix_length, content_tag, tagnames_stripped;
    var synthesize_pattern = new RegExp('\{([^\{]*)\}', 'gi');

    root_tag = (opts['root_tag']) ? opts['root_tag'] : '';
    track_tag = (opts['track_tag']) ? opts['track_tag'] : '';
    track_segment_tag = (opts['track_segment_tag']) ? opts['track_segment_tag'] : '';
    track_point_tag = (opts['track_point_tag']) ? opts['track_point_tag'] : '';
    marker_tag = (opts['marker_tag']) ? opts['marker_tag'] : '';
    tag_prefix = (opts['tag_prefix']) ? opts['tag_prefix'] : '';
    prefix_length = tag_prefix.length;
    content_tag = (opts['content_tag']) ? opts['content_tag'] : '';
    tagnames_stripped = (opts['tagnames_stripped']) ? true : false;

    // if (self.gv_json_script) { gv_json_script.removeScriptTag(); } // this fouls things up if multiple files are loaded almost simultaneously!

    var marker_default_fields = ['icon', 'color', 'opacity', 'icon_size', 'icon_anchor'];
    if (opts['default_marker']) {
        if (opts['default_marker']['size']) {
            opts['default_marker']['icon_size'] = opts['default_marker']['size'];
        }
        if (opts['default_marker']['anchor']) {
            opts['default_marker']['icon_anchor'] = opts['default_marker']['anchor'];
        }
        if (opts['default_marker']['icon_size'] && opts['default_marker']['icon_size'].length < 2 && opts['default_marker']['icon_size'].match(/([0-9]+)[^0-9]+([0-9]+)/)) {
            var parts = opts['default_marker']['icon_size'].match(/([0-9-]+)[^0-9-]+([0-9-]+)/);
            opts['default_marker']['icon_size'] = [parts[1], parts[2]];
        }
        if (opts['default_marker']['icon_anchor'] && opts['default_marker']['icon_anchor'].length < 2 && opts['default_marker']['icon_anchor'].match(/([0-9]+)[^0-9]+([0-9]+)/)) {
            var parts = opts['default_marker']['icon_anchor'].match(/([0-9-]+)[^0-9-]+([0-9-]+)/);
            opts['default_marker']['icon_anchor'] = [parts[1], parts[2]];
        }
    }

    var filter_regexp = null;
    var filter_field = null;
    if (opts['filter'] && opts['filter']['field'] && opts['filter']['pattern']) {
        filter_regexp = new RegExp(opts['filter']['pattern'], 'i');
        filter_field = (tagnames_stripped) ? opts['filter']['field'].replace(/[^A-Za-z0-9\.]/gi, '').toLowerCase() : opts['filter']['field'].toLowerCase();
    }

    map_name = (self.gv_options && gv_options['map']) ? gv_options['map'] : 'gmap';
    var map = eval('self.' + map_name);
    if (!map) {
        return false;
    }
    marker_array_name = (self.gv_options && gv_options['marker_array']) ? gv_options['marker_array'] : 'wpts';
    var marker_array = eval('self.' + marker_array_name);
    if (!marker_array) {
        wpts = [];
        marker_array = wpts;
        gv_options['marker_array'] = 'wpts';
    }
    track_array_name = (self.gv_options && gv_options['track_array']) ? gv_options['track_array'] : 'trk';
    var track_array = eval('self.' + track_array_name);
    track_info_array_name = (self.gv_options && gv_options['track_info_array']) ? gv_options['track_info_array'] : 'trk_info';
    var track_info_array = eval('self.' + track_info_array_name);
    track_segments_array_name = (self.gv_options && gv_options['track_segments_array']) ? gv_options['track_segments_array'] : 'trk_segments';
    var track_segments_array = eval('self.' + track_segments_array_name);
    if (!self.track_array) {
        trk = [];
        track_array = trk;
        gv_options['track_array'] = 'trk';
    }
    if (!self.track_info_array) {
        trk_info = [];
        track_info_array = trk_info;
        gv_options['track_info_array'] = 'trk_info';
    }
    if (!self.track_segments_array) {
        trk_segments = [];
        track_segments_array = trk_segments;
        gv_options['track_segments_array'] = 'trk_segments';
    }

    if (!root_tag) {
        for (var rt in data) {
            if (!root_tag && data[rt]) {
                root_tag = rt;
            }
        }
    }

    // Set things properly for GPX files
    if (root_tag == 'gpx') {
        marker_tag = (marker_tag != '') ? marker_tag : 'wpt';
        track_tag = (track_tag != '') ? track_tag : 'trk';
        track_segment_tag = (track_segment_tag != '') ? track_segment_tag : 'trkseg';
        track_point_tag = (track_point_tag != '') ? track_point_tag : 'trkpt';
        tag_prefix = '';
        content_tag = '';
        tagnames_stripped = false;
    }

    // re-process GeoJSON files into a more GPX-like simple marker structure:
    if ((root_tag == 'type' && data[root_tag] == 'FeatureCollection') || root_tag == 'features') {
        var new_data = [];
        new_data['markers'] = [];
        new_data['markers']['marker'] = [];
        for (var i = 0; i < data['features'].length; i++) {
            var feature = data['features'][i];
            var m = [];
            if (feature['geometry'] && feature['geometry']['coordinates'] && feature['geometry']['coordinates'].length > 1) {
                m['longitude'] = feature['geometry']['coordinates'][0];
                m['latitude'] = feature['geometry']['coordinates'][1];
                if (feature['properties']) {
                    for (var prop in feature['properties']) {
                        m[prop] = feature['properties'][prop];
                    }
                    if (feature['properties']['photoUrl']) {
                        m['photo'] = feature['properties']['photoUrl'];
                    }
                    if (feature['properties']['photoWidth'] && feature['properties']['photoHeight']) {
                        m['photo_size'] = feature['properties']['photoWidth'] + ',' + feature['properties']['photoHeight'];
                    } else if (feature['properties']['photoWidth']) {
                        m['photo_width'] = feature['properties']['photoWidth'];
                    }
                    if (feature['properties']['placardUrl']) {
                        m['icon'] = feature['properties']['placardUrl'];
                    }
                    if (feature['properties']['placardWidth'] && feature['properties']['placardHeight']) {
                        m['icon_size'] = feature['properties']['placardWidth'] + ',' + feature['properties']['placardHeight'];
                        if (feature['properties']['placardUrl'] && feature['properties']['placardUrl'].match(/google\..*\/latitude\/apps\/badge.*photo_placard/)) {
                            m['icon_anchor'] = parseInt(0.5 + feature['properties']['placardWidth'] / 2) + ',' + feature['properties']['placardHeight'];
                        }
                    }
                }
                new_data['markers']['marker'].push(m);
            }
        }
        root_tag = 'markers';
        marker_tag = 'marker';
        data = new_data;
    }

    if (root_tag && !marker_tag) {
        if (root_tag == 'feed' && data[root_tag]['entry']) {
            marker_tag = 'entry';
        } else {
            for (var mt in data[root_tag]) {
                if (!marker_tag && data[root_tag][mt]) {
                    marker_tag = mt;
                }
            }
        }
    }

    if (root_tag == 'kml') { // it's a KML file

        if (data[root_tag]['Document'] || data[root_tag]['Folder'] || data[root_tag]['Placemark']) {
            if (!data[root_tag]['Document']) { // there's no "Document" tag!
                data[root_tag]['Document'] = [data[root_tag]];
            } else if (!data[root_tag]['Document'].length) { // if there's only one, it has no length; make it into an array
                data[root_tag]['Document'] = [data[root_tag]['Document']];
            }
            var marker_count = 0;
            var marker_styles = [];
            var track_styles = [];
            var polygon_styles = [];
            for (var i = 0; i < data[root_tag]['Document'].length; i++) {
                var doc = data[root_tag]['Document'][i];
                if (doc['Style']) { // record all global styles for future reference
                    if (!doc['Style'].length) {
                        doc['Style'] = [doc['Style']];
                    } // if there's only one, it has no length; make it into an array
                    for (var j = 0; j < doc['Style'].length; j++) {
                        if (doc['Style'][j]['id'] && doc['Style'][j]['IconStyle']) { // marker styles
                            var ist = doc['Style'][j]['IconStyle'];
                            var this_style = [];
                            this_style['icon'] = (ist['Icon'] && ist['Icon']['href']) ? ist['Icon']['href'] : null;
                            this_style['scale'] = (ist['scale']) ? parseFloat(ist['scale']) : null;
                            if (ist['color'] && ist['color'].length == 8) {
                                this_style['color'] = '#' + ist['color'].replace(/\w\w(\w\w)(\w\w)(\w\w)/, '$3$2$1');
                                this_style['opacity'] = parseInt(ist['color'].replace(/(\w\w)\w\w\w\w\w\w/, '$1'), 16) / 255;
                            }
                            if (ist['hotSpot'] && ist['hotSpot']['xunits'] && ist['hotSpot']['yunits'] && ist['hotSpot']['xunits'] == 'pixels' && ist['hotSpot']['yunits'] == 'pixels') {
                                this_style['icon_anchor'] = [parseFloat(ist['hotSpot']['x']), 32 - parseFloat(ist['hotSpot']['y'])];
                            } else if (ist['hotSpot'] && ist['hotSpot']['xunits'] && ist['hotSpot']['yunits'] && ist['hotSpot']['xunits'] == 'fraction' && ist['hotSpot']['yunits'] == 'fraction') {
                                this_style['icon_anchor'] = [32 * parseFloat(ist['hotSpot']['x']), 32 * (1 - parseFloat(ist['hotSpot']['y']))];
                            }
                            marker_styles[ doc['Style'][j]['id'] ] = this_style;
                        }
                        if (doc['Style'][j]['id'] && doc['Style'][j]['LineStyle']) { // track/polyline styles
                            var lst = doc['Style'][j]['LineStyle'];
                            var this_style = [];
                            if (lst['color'] && lst['color'].length == 8) {
                                this_style['color'] = '#' + lst['color'].replace(/\w\w(\w\w)(\w\w)(\w\w)/, '$3$2$1');
                                this_style['opacity'] = parseInt(lst['color'].replace(/(\w\w)\w\w\w\w\w\w/, '$1'), 16) / 255;
                            }
                            if (lst['width']) {
                                this_style['width'] = parseFloat(lst['width']);
                            }
                            track_styles[ doc['Style'][j]['id'] ] = this_style;
                        }
                        if (doc['Style'][j]['id'] && doc['Style'][j]['PolyStyle']) { // polygon fill styles
                            var pst = doc['Style'][j]['PolyStyle'];
                            var this_style = new Array();
                            if (pst['color'] && pst['color'].length == 8) {
                                this_style['fill_color'] = '#' + pst['color'].replace(/\w\w(\w\w)(\w\w)(\w\w)/, '$3$2$1');
                                this_style['fill_opacity'] = parseInt(pst['color'].replace(/(\w\w)\w\w\w\w\w\w/, '$1'), 16) / 255;
                            }
                            polygon_styles[ doc['Style'][j]['id'] ] = this_style;
                        }
                    }
                }
                if (doc['StyleMap']) { // Examine <StyleMap> tags and see if they have existing style URLs in them; if so, we can handle that
                    if (!doc['StyleMap'].length) {
                        doc['StyleMap'] = [doc['StyleMap']];
                    } // if there's only one, it has no length; make it into an array
                    for (var j = 0; j < doc['StyleMap'].length; j++) {
                        var sm = doc['StyleMap'][j];
                        if (sm['Pair']) {
                            if (!sm['Pair'].length) {
                                sm['Pair'] = [sm['Pair']];
                            } // if there's only one, it has no length; make it into an array
                            for (var k = 0; k < sm['Pair'].length; k++) {
                                if (sm['Pair'][k]['key'] && sm['Pair'][k]['key'] == 'normal' && sm['Pair'][k]['styleUrl']) {
                                    var style_id = sm['Pair'][k]['styleUrl'].replace(/^#/, '');
                                    if (marker_styles[style_id]) {
                                        marker_styles[sm['id']] = marker_styles[style_id];
                                    }
                                    if (track_styles[style_id]) {
                                        track_styles[sm['id']] = track_styles[style_id];
                                    }
                                    if (polygon_styles[style_id]) {
                                        polygon_styles[sm['id']] = polygon_styles[style_id];
                                    }
                                }
                            }
                        }
                    }
                }
                if (doc['Folder']) {
                    if (!doc['Placemark']) {
                        doc['Placemark'] = [];
                    } // Placemarks must be made into a proper array, because things need to be "push"ed into it
                    else if (doc['Placemark'] && !doc['Placemark'].length) {
                        doc['Placemark'] = [doc['Placemark']];
                    } // Placemarks must be made into a proper array, because things need to be "push"ed into it
                    if (!doc['Folder'].length) {
                        doc['Folder'] = [doc['Folder']];
                    } // if there's only one, it has no length; make it into an array
                    for (var j = 0; j < doc['Folder'].length; j++) {
                        var folder = doc['Folder'][j];
                        var folder_name = (folder['name']) ? folder['name'] : '';
                        if (folder['Placemark']) {
                            if (!folder['Placemark'].length) {
                                folder['Placemark'] = [folder['Placemark']];
                            }
                            for (var k = 0; k < folder['Placemark'].length; k++) {
                                var pm = folder['Placemark'][k];
                                pm['folder'] = folder_name;
                                doc['Placemark'].push(pm);
                            }
                        }
                        // we'll look for two levels of folders below the top level, but no more than that!!
                        if (folder['Folder']) {
                            if (!folder['Folder'].length) {
                                folder['Folder'] = [folder['Folder']];
                            } // if there's only one, it has no length; make it into an array
                            for (var k = 0; k < folder['Folder'].length; k++) {
                                var subfolder = folder['Folder'][k];
                                var subfolder_name = (subfolder['name']) ? subfolder['name'] : '';
                                if (subfolder['Placemark']) {
                                    if (!subfolder['Placemark'].length) {
                                        subfolder['Placemark'] = [subfolder['Placemark']];
                                    }
                                    for (var l = 0; l < subfolder['Placemark'].length; l++) {
                                        var pm = subfolder['Placemark'][l];
                                        pm['folder'] = subfolder_name;
                                        doc['Placemark'].push(pm);
                                    }
                                }
                            }
                        }
                    }
                }
                if (doc['Placemark']) {
                    if (!doc['Placemark'].length) {
                        doc['Placemark'] = [doc['Placemark']];
                    } // if there's only one, it has no length; make it into an array
                    for (var j = 0; j < doc['Placemark'].length; j++) {
                        var pm = doc['Placemark'][j];
                        var marker_data = [];
                        if ((pm['Point'] && pm['Point']['coordinates']) || (pm['MultiGeometry'] && pm['MultiGeometry']['Point'] && pm['MultiGeometry']['Point']['coordinates']) || (pm['GeometryCollection'] && pm['GeometryCollection']['Point'] && pm['GeometryCollection']['Point']['coordinates'])) { // WAYPOINT
                            if (!pm['Point']) {
                                pm['Point'] = [];
                            } else if (!pm['Point'].length) {
                                pm['Point'] = [pm['Point']];
                            }
                            if (pm['MultiGeometry'] && pm['MultiGeometry']['Point'] && pm['MultiGeometry']['Point']['coordinates']) { // dump these into the general Point collection
                                if (!pm['MultiGeometry']['Point'].length) {
                                    pm['MultiGeometry']['Point'] = [pm['MultiGeometry']['Point']];
                                }
                                for (var k = 0; k < pm['MultiGeometry']['Point'].length; k++) {
                                    pm['Point'].push(pm['MultiGeometry']['Point'][k]);
                                }
                            }
                            if (pm['GeometryCollection'] && pm['GeometryCollection']['Point']) { // dump these into the general LineString collection
                                if (!pm['GeometryCollection']['Point'].length) {
                                    pm['GeometryCollection']['Point'] = [pm['GeometryCollection']['Point']];
                                }
                                for (var k = 0; k < pm['GeometryCollection']['Point'].length; k++) {
                                    pm['Point'].push(pm['GeometryCollection']['Point'][k]);
                                }
                            }
                            for (var p = 0; p < pm['Point'].length; p++) {
                                if (pm['Point'][p]['coordinates']) {
                                    var parts = pm['Point'][p]['coordinates'].split(',');
                                    marker_data['lon'] = parseFloat(parts[0]);
                                    marker_data['lat'] = parseFloat(parts[1]);
                                    marker_data['ele'] = parseFloat(parts[2]);
                                    if (isNaN(marker_data['lat']) || isNaN(marker_data['lon']) || (marker_data['lat'] == 0 && marker_data['lon'] == 0) || Math.abs(marker_data['lat']) > 90 || Math.abs(marker_data['lon']) > 180 || marker_data['lat'] == undefined || marker_data['lon'] == undefined) {
                                        // invalid coordinates; but note that bad coordinates are the ONLY thing that will prevent a marker from being added to the map
                                    } else {
                                        marker_data['name'] = (pm['name']) ? (pm['name']) : '';
                                        marker_data['desc'] = (pm['description']) ? pm['description'].replace(/(&nbsp;)+$/g, '') : '';
                                        marker_data['folder'] = (pm['folder']) ? (pm['folder']) : '';
                                        if (opts['ignore_styles']) {
                                            // colors, icons, etc. in the remote data will be ignored
                                        } else {
                                            if (pm['styleUrl']) { // check for a global style that might be applied
                                                var style_id = pm['styleUrl'].replace(/^#/, '');
                                                if (marker_styles[style_id]) {
                                                    for (var attr in marker_styles[style_id]) {
                                                        marker_data[attr] = marker_styles[style_id][attr];
                                                    }
                                                }
                                            }
                                            if (pm['Style'] && pm['Style']['IconStyle']) { // local styles override globals
                                                var ist = pm['Style']['IconStyle'];
                                                if (ist['Icon'] && ist['Icon']['href']) {
                                                    marker_data['icon'] = ist['Icon']['href'];
                                                }
                                                if (ist['scale']) {
                                                    marker_data['scale'] = parseFloat(ist['scale']);
                                                }
                                                if (ist['color'] && ist['color'].length == 8) {
                                                    marker_data['color'] = '#' + ist['color'].replace(/\w\w(\w\w)(\w\w)(\w\w)/, '$3$2$1');
                                                    marker_data['opacity'] = parseInt(ist['color'].replace(/(\w\w)\w\w\w\w\w\w/, '$1'), 16) / 255;
                                                }
                                                if (ist['hotSpot'] && ist['hotSpot']['xunits'] && ist['hotSpot']['yunits'] && ist['hotSpot']['xunits'] == 'pixels' && ist['hotSpot']['yunits'] == 'pixels') {
                                                    marker_data['icon_anchor'] = [parseFloat(ist['hotSpot']['x']), 32 - parseFloat(ist['hotSpot']['y'])];
                                                } else if (ist['hotSpot'] && ist['hotSpot']['xunits'] && ist['hotSpot']['yunits'] && ist['hotSpot']['xunits'] == 'fraction' && ist['hotSpot']['yunits'] == 'fraction') {
                                                    marker_data['icon_anchor'] = [32 * parseFloat(ist['hotSpot']['x']), 32 * (1 - parseFloat(ist['hotSpot']['y']))];
                                                }
                                            } else if (pm['StyleMap'] && pm['StyleMap']['Pair']) {
                                                if (!pm['StyleMap']['Pair'].length) {
                                                    pm['StyleMap']['Pair'] = [pm['StyleMap']['Pair']];
                                                } // if there's only one, it has no length; make it into an array
                                                for (var k = 0; k < pm['StyleMap']['Pair'].length; k++) {
                                                    if (pm['StyleMap']['Pair'][k]['key'] && pm['StyleMap']['Pair'][k]['key'] == 'normal' && pm['StyleMap']['Pair'][k]['Style']) {
                                                        var st = pm['StyleMap']['Pair'][k]['Style'];
                                                        if (st['IconStyle']) {
                                                            var ist = st['IconStyle'];
                                                            if (ist['Icon'] && ist['Icon']['href']) {
                                                                marker_data['icon'] = ist['Icon']['href'];
                                                            }
                                                            if (ist['scale']) {
                                                                marker_data['scale'] = parseFloat(ist['scale']);
                                                            }
                                                            if (ist['color'] && ist['color'].length == 8) {
                                                                marker_data['color'] = '#' + ist['color'].replace(/\w\w(\w\w)(\w\w)(\w\w)/, '$3$2$1');
                                                                marker_data['opacity'] = parseInt(ist['color'].replace(/(\w\w)\w\w\w\w\w\w/, '$1'), 16) / 255;
                                                            }
                                                            if (ist['hotSpot'] && ist['hotSpot']['xunits'] && ist['hotSpot']['yunits'] && ist['hotSpot']['xunits'] == 'pixels' && ist['hotSpot']['yunits'] == 'pixels') {
                                                                marker_data['icon_anchor'] = [parseFloat(ist['hotSpot']['x']), 32 - parseFloat(ist['hotSpot']['y'])];
                                                            } else if (ist['hotSpot'] && ist['hotSpot']['xunits'] && ist['hotSpot']['yunits'] && ist['hotSpot']['xunits'] == 'fraction' && ist['hotSpot']['yunits'] == 'fraction') {
                                                                marker_data['icon_anchor'] = [32 * parseFloat(ist['hotSpot']['x']), 32 * (1 - parseFloat(ist['hotSpot']['y']))];
                                                            }
                                                        }
                                                        if (st['BalloonStyle']) {
                                                            var bst = st['BalloonStyle'];
                                                            var balloon_text = bst['text'].replace(/\$\[(\w+)\]/g, function(complete_match, field_name) {
                                                                return (pm[field_name]) ? pm[field_name] : '';
                                                            });
                                                            balloon_text = balloon_text.replace(/\s*(<br\/?>\s*|&#160;\s*)*\s*$/g, ''); // remove white space from end of "balloon"
                                                            if (balloon_text) {
                                                                marker_data['desc'] = balloon_text;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (opts['synthesize_fields']) {
                                            for (var f in opts['synthesize_fields']) {
                                                if (opts['synthesize_fields'][f]) {
                                                    var template = opts['synthesize_fields'][f];
                                                    template = template.replace(synthesize_pattern,
                                                            function(complete_match, field_name) {
                                                                if (marker_data[field_name] || marker_data[field_name] == '0' || marker_data[field_name] === false) {
                                                                    return (marker_data[field_name]);
                                                                } else {
                                                                    return ('');
                                                                }
                                                            }
                                                    );
                                                    marker_data[f] = (template.match(/^\s*$/)) ? '' : template;
                                                }
                                            }
                                        }
                                        if (marker_data['icon'] && marker_data['icon'].match(/google|gstatic/)) {
                                            marker_data = GV_KML_Icon_Anchors(marker_data);
                                        }
                                        var marker_ok = true;
                                        if (filter_regexp && filter_field) {
                                            if (marker_data[filter_field] && marker_data[filter_field].toString().match(filter_regexp)) {
                                                marker_ok = true;
                                            } else {
                                                marker_ok = false;
                                            }
                                        }
                                        if (marker_ok) {
                                            marker_data['dynamic'] = gv_dynamic_file_index + 1; // +1 so we can test for its presence!
                                            marker_array.push(GV_Marker(map, marker_data));
                                            marker_count++;
                                        }
                                    } // end if valid coordinates
                                } // end if pm['Point'][p]['coordinates']
                            } // end for loop (pm['Point'].length)
                        } else if ((pm['MultiGeometry'] && pm['MultiGeometry']['LineString']) || (pm['MultiGeometry'] && pm['MultiGeometry']['Polygon']) || (pm['GeometryCollection'] && pm['GeometryCollection']['LineString']) || pm['LineString'] || pm['Polygon']) { // TRACK
                            var tn = (!track_array.length) ? 1 : track_array.length + 1; // tn = track number
                            track_array[tn] = [];
                            track_info_array[tn] = [];
                            track_info_array[tn]['name'] = (pm['name']) ? pm['name'] : '[track]';
                            track_info_array[tn]['desc'] = (pm['description']) ? pm['description'].replace(/(&nbsp;)+$/g, '') : '';
                            track_info_array[tn]['width'] = (opts['track_options'] && opts['track_options']['width']) ? parseFloat(opts['track_options']['width']) : 4; // defaults
                            track_info_array[tn]['color'] = (opts['track_options'] && opts['track_options']['color']) ? opts['track_options']['color'] : '#ff0000'; // defaults
                            track_info_array[tn]['opacity'] = (opts['track_options'] && opts['track_options']['opacity']) ? parseFloat(opts['track_options']['opacity']) : 0.8; // defaults
                            if (pm['Polygon'] || (pm['MultiGeometry'] && pm['MultiGeometry']['Polygon'])) {
                                track_info_array[tn]['fill_color'] = (opts['track_options'] && opts['track_options']['fill_color']) ? opts['track_options']['fill_color'] : ''; // defaults
                                track_info_array[tn]['fill_opacity'] = (opts['track_options'] && opts['track_options']['fill_opacity']) ? parseFloat(opts['track_options']['fill_opacity']) : 0; // defaults
                            }
                            track_info_array[tn]['clickable'] = (opts['track_options'] && opts['track_options']['clickable'] === false) ? false : true; // defaults
                            if (opts['ignore_styles']) {
                                // colors, icons, etc. in the remote data will be ignored
                            } else {
                                if (pm['styleUrl']) { // check for a global style that might be applied
                                    var style_id = pm['styleUrl'].replace(/^#/, '');
                                    if (track_styles[style_id]) {
                                        for (var attr in track_styles[style_id]) {
                                            track_info_array[tn][attr] = track_styles[style_id][attr];
                                        }
                                    }
                                    if ((pm['Polygon'] || (pm['MultiGeometry'] && pm['MultiGeometry']['Polygon'])) && polygon_styles[style_id]) {
                                        for (var attr in polygon_styles[style_id]) {
                                            track_info_array[tn][attr] = polygon_styles[style_id][attr];
                                        }
                                    }
                                }
                                if (pm['Style'] && pm['Style']['LineStyle']) { // local styles override globals
                                    var lst = pm['Style']['LineStyle'];
                                    if (lst['color'] && lst['color'].length == 8) {
                                        track_info_array[tn]['color'] = '#' + lst['color'].replace(/\w\w(\w\w)(\w\w)(\w\w)/, '$3$2$1');
                                        track_info_array[tn]['opacity'] = parseInt(lst['color'].replace(/(\w\w)\w\w\w\w\w\w/, '$1'), 16) / 255;
                                    }
                                    if (lst['width']) {
                                        track_info_array[tn]['width'] = parseFloat(lst['width']);
                                    }
                                }
                                if ((pm['Polygon'] || (pm['MultiGeometry'] && pm['MultiGeometry']['Polygon'])) && pm['Style'] && pm['Style']['PolyStyle']) { // local styles override globals
                                    var pst = pm['Style']['PolyStyle'];
                                    if (pst['color'] && pst['color'].length == 8) {
                                        track_info_array[tn]['fill_color'] = '#' + pst['color'].replace(/\w\w(\w\w)(\w\w)(\w\w)/, '$3$2$1');
                                        track_info_array[tn]['fill_opacity'] = parseInt(pst['color'].replace(/(\w\w)\w\w\w\w\w\w/, '$1'), 16) / 255;
                                    }
                                }
                            }
                            if (!pm['LineString']) { // there are no LineStrings in the Placemark yet
                                pm['LineString'] = [];
                            } else if (pm['LineString'] && !pm['LineString'].length) { // if there's only one, it has no length; make it into an array
                                pm['LineString'] = [pm['LineString']];
                            }
                            if (pm['MultiGeometry'] && pm['MultiGeometry']['LineString']) { // dump these into the general LineString collection
                                if (!pm['MultiGeometry']['LineString'].length) {
                                    pm['MultiGeometry']['LineString'] = [pm['MultiGeometry']['LineString']];
                                }
                                for (var k = 0; k < pm['MultiGeometry']['LineString'].length; k++) {
                                    pm['LineString'].push(pm['MultiGeometry']['LineString'][k]);
                                }
                            }
                            if (pm['GeometryCollection'] && pm['GeometryCollection']['LineString']) { // dump these into the general LineString collection
                                if (!pm['GeometryCollection']['LineString'].length) {
                                    pm['GeometryCollection']['LineString'] = [pm['GeometryCollection']['LineString']];
                                }
                                for (var k = 0; k < pm['GeometryCollection']['LineString'].length; k++) {
                                    pm['LineString'].push(pm['GeometryCollection']['LineString'][k]);
                                }
                            }
                            if (pm['Polygon'] && pm['Polygon']['outerBoundaryIs'] && pm['Polygon']['outerBoundaryIs']['LinearRing']) { // dump these into the general LineString collection
                                var polygon_boundary = pm['Polygon']['outerBoundaryIs'];
                                if (polygon_boundary['LinearRing'] && !polygon_boundary['LinearRing'].length) {
                                    polygon_boundary['LinearRing'] = [polygon_boundary['LinearRing']];
                                }
                                for (var k = 0; k < polygon_boundary['LinearRing'].length; k++) {
                                    pm['LineString'].push(polygon_boundary['LinearRing'][k]);
                                }
                            }
                            if (pm['MultiGeometry'] && pm['MultiGeometry']['Polygon'] && pm['MultiGeometry']['Polygon']['outerBoundaryIs'] && pm['MultiGeometry']['Polygon']['outerBoundaryIs']['LinearRing']) { // dump these into the general LineString collection
                                var polygon_boundary = pm['MultiGeometry']['Polygon']['outerBoundaryIs'];
                                if (polygon_boundary['LinearRing'] && !polygon_boundary['LinearRing'].length) {
                                    polygon_boundary['LinearRing'] = [polygon_boundary['LinearRing']];
                                }
                                for (var k = 0; k < polygon_boundary['LinearRing'].length; k++) {
                                    pm['LineString'].push(polygon_boundary['LinearRing'][k]);
                                }
                            }
                            var segment_limit = 2000; // doesn't seem to matter much anymore, so we'll just set it high
                            var coord_count = 0;
                            var lat_sum = null;
                            var lon_sum = null;
                            var bounds_e = null;
                            var bounds_w = null;
                            var bounds_n = null;
                            var bounds_s = null;
                            for (var k = 0; k < pm['LineString'].length; k++) {
                                if (pm['LineString'][k]['coordinates']) {
                                    var pts = [];
                                    if (pm['LineString'][k]['coordinates'].match(/, /)) {
                                        pm['LineString'][k]['coordinates'] = pm['LineString'][k]['coordinates'].replace(/, +/g, ',');
                                    }
                                    var coords = pm['LineString'][k]['coordinates'].split(/\s+/);
                                    var last_point = null;
                                    for (var l = 0; l < coords.length; l++) {
                                        var parts = coords[l].split(',');
                                        var lon = parseFloat(parts[0]);
                                        var lat = parseFloat(parts[1]);
                                        var ele = parseFloat(parts[2]);
                                        if (Math.abs(lat) <= 90 && Math.abs(lon) <= 180) {
                                            pts.push(new GLatLng(lat, lon));
                                            lat_sum += lat;
                                            lon_sum += lon;
                                            coord_count += 1;
                                            if (!bounds_w || lon < bounds_w) {
                                                bounds_w = lon;
                                            }
                                            if (!bounds_e || lon > bounds_e) {
                                                bounds_e = lon;
                                            }
                                            if (!bounds_s || lat < bounds_s) {
                                                bounds_s = lat;
                                            }
                                            if (!bounds_n || lat > bounds_n) {
                                                bounds_n = lat;
                                            }
                                            if (pts.length % segment_limit == 0) {
                                                pts.unshift(last_point);
                                                if (track_info_array[tn]['fill_opacity'] && track_info_array[tn]['fill_opacity'] > 0) {
                                                    var fill_color = (track_info_array[tn]['fill_color']) ? track_info_array[tn]['fill_color'] : track_info_array[tn]['color'];
                                                    track_array[tn].push(new GPolygon(pts, GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['width'], track_info_array[tn]['opacity'], GV_Color_Name2Hex(fill_color), track_info_array[tn]['fill_opacity'], {mouseOutTolerance: 1}));
                                                } else {
                                                    track_array[tn].push(new GPolyline(pts, GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['width'], track_info_array[tn]['opacity'], {mouseOutTolerance: 1}));
                                                }
                                                map.addOverlay(track_array[tn][ track_array[tn].length - 1 ]);
                                                last_point = new GLatLng(lat, lon);
                                                pts = [];
                                            }
                                        }
                                    }
                                    if (pts.length > 0) {
                                        if (last_point) {
                                            pts.unshift(last_point);
                                        }
                                        if (track_info_array[tn]['fill_opacity'] && track_info_array[tn]['fill_opacity'] > 0) {
                                            var fill_color = (track_info_array[tn]['fill_color']) ? track_info_array[tn]['fill_color'] : track_info_array[tn]['color'];
                                            track_array[tn].push(new GPolygon(pts, GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['width'], track_info_array[tn]['opacity'], GV_Color_Name2Hex(fill_color), track_info_array[tn]['fill_opacity'], {mouseOutTolerance: 1}));
                                        } else {
                                            track_array[tn].push(new GPolyline(pts, GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['width'], track_info_array[tn]['opacity'], {mouseOutTolerance: 1}));
                                        }
                                        map.addOverlay(track_array[tn][ track_array[tn].length - 1 ]);
                                    }
                                }
                            }
                            if (coord_count) {
                                track_info_array[tn]['center'] = [];
                                track_info_array[tn]['center']['lat'] = lat_sum / coord_count;
                                track_info_array[tn]['center']['lon'] = lon_sum / coord_count;
                            }
                            if (bounds_s != null && bounds_n != null && bounds_w != null && bounds_e != null) {
                                track_info_array[tn]['bounds'] = [];
                                track_info_array[tn]['bounds']['s'] = bounds_s;
                                track_info_array[tn]['bounds']['n'] = bounds_n;
                                track_info_array[tn]['bounds']['w'] = bounds_w;
                                track_info_array[tn]['bounds']['e'] = bounds_e;
                            }
//							trk = track_array; trk_info = track_info_array;
                            GV_Finish_Track(tn);
                            GV_Add_Track_to_Tracklist({bullet: '- ', name: track_info_array[tn]['name'], desc: track_info_array[tn]['desc'], color: track_info_array[tn]['color'], map_name: map_name, id: track_array_name + "[" + tn + "]"});
                        }
                    }
                }
            }
        }

    } else { // not a KML file

        if (data && data[root_tag] && ((track_tag && data[root_tag][track_tag]) || (track_point_tag && data[root_tag][track_point_tag]))) {
            if (track_tag && data[root_tag][track_tag] && !data[root_tag][track_tag].length) {
                data[root_tag][track_tag] = [data[root_tag][track_tag]];
            }
            if (track_point_tag && data[root_tag][track_point_tag] && !data[root_tag][track_point_tag].length) {
                data[root_tag][track_point_tag] = [data[root_tag][track_point_tag]];
            }
            var trk_default_name = (opts['track_options'] && opts['track_options']['name']) ? opts['track_options']['name'] : '[track]'; // defaults
            var trk_default_desc = (opts['track_options'] && opts['track_options']['desc']) ? opts['track_options']['desc'] : ''; // defaults
            var trk_default_width = (opts['track_options'] && opts['track_options']['width']) ? parseFloat(opts['track_options']['width']) : 3; // defaults
            var trk_default_color = (opts['track_options'] && opts['track_options']['color']) ? opts['track_options']['color'] : '#E600E6'; // defaults
            var trk_default_opacity = (opts['track_options'] && opts['track_options']['opacity']) ? parseFloat(opts['track_options']['opacity']) : 0.8; // defaults
            var trk_default_fill_color = (opts['track_options'] && opts['track_options']['fill_color']) ? opts['track_options']['fill_color'] : '#E600E6'; // defaults
            var trk_default_fill_opacity = (opts['track_options'] && opts['track_options']['fill_opacity']) ? parseFloat(opts['track_options']['fill_opacity']) : 0; // defaults
            if (!track_tag) {
                track_tag = 'gv_tracks';
                data[root_tag]['gv_tracks'] = [data[root_tag]];
            }
            var tracks = (track_tag && data[root_tag][track_tag]) ? data[root_tag][track_tag] : [data[root_tag]];
            for (var i = 0; i < tracks.length; i++) {
                var this_trk = tracks[i];
                var add_to_tracklist = false;
                if ((track_segment_tag && this_trk[track_segment_tag]) || (track_point_tag && this_trk[track_point_tag])) {
                    var tn = (!track_array.length) ? 1 : track_array.length; // tn = track number
                    track_array[tn] = [];
                    track_info_array[tn] = [];
                    track_info_array[tn]['name'] = (this_trk['name']) ? this_trk['name'] : trk_default_name;
                    track_info_array[tn]['desc'] = (this_trk['desc']) ? this_trk['desc'] : trk_default_desc;
                    track_info_array[tn]['clickable'] = (opts['track_options'] && opts['track_options']['clickable'] === false) ? false : true; // defaults
                    track_info_array[tn]['no_list'] = ((opts['track_options'] && opts['track_options']['no_list']) || this_trk['no_list']) ? true : false; // defaults
                    if (opts['ignore_styles']) {
                        track_info_array[tn]['width'] = trk_default_width;
                        track_info_array[tn]['color'] = trk_default_color;
                        track_info_array[tn]['opacity'] = trk_default_opacity;
                        track_info_array[tn]['fill_color'] = trk_default_fill_color;
                        track_info_array[tn]['fill_opacity'] = trk_default_fill_opacity;
                    } else {
                        track_info_array[tn]['width'] = (this_trk['width']) ? parseFloat(this_trk['width']) : trk_default_width;
                        track_info_array[tn]['color'] = (this_trk['color']) ? this_trk['color'] : trk_default_color;
                        track_info_array[tn]['opacity'] = (this_trk['opacity']) ? parseFloat(this_trk['opacity']) : trk_default_opacity;
                        track_info_array[tn]['fill_color'] = (this_trk['fill_color']) ? this_trk['fill_color'] : trk_default_fill_color;
                        track_info_array[tn]['fill_opacity'] = (this_trk['fill_opacity']) ? parseFloat(this_trk['fill_opacity']) : trk_default_fill_opacity;
                    }
                    var lat_sum = null;
                    var lon_sum = null;
                    var bounds_e = null;
                    var bounds_w = null;
                    var bounds_n = null;
                    var bounds_s = null;
                    var coord_count = 0;
                    if (track_segment_tag && this_trk[track_segment_tag]) {
                        if (!this_trk[track_segment_tag].length) {
                            this_trk[track_segment_tag] = [this_trk[track_segment_tag]];
                        }
                        var lat_alias = 'lat';
                        var lon_alias = 'lon';
                        if (this_trk[track_segment_tag][0][track_point_tag] && this_trk[track_segment_tag][0][track_point_tag].length) {
                            for (var field in this_trk[track_segment_tag][0][track_point_tag][0]) { // for efficiency, only search the first point for latitude & longitude tags
                                var field_cropped = field.substring(prefix_length);
                                if (field_cropped.match(/^(lati?|latt?itude)\b/i)) {
                                    lat_alias = field_cropped;
                                }
                                else if (field_cropped.match(/^(long?|lng|long?t?itude)\b/i)) {
                                    lon_alias = field_cropped;
                                }
                            }
                            for (var j = 0; j < this_trk[track_segment_tag].length; j++) {
                                var trkseg = this_trk[track_segment_tag][j];
                                if (!trkseg[track_point_tag].length) {
                                    trkseg[track_point_tag] = [trkseg[track_point_tag]];
                                }
                                var pts = [];
                                for (var k = 0; k < trkseg[track_point_tag].length; k++) {
                                    var lat = 91;
                                    var lon = 181;
                                    if (content_tag) {
                                        if (trkseg[track_point_tag][k][tag_prefix + lat_alias] && trkseg[track_point_tag][k][tag_prefix + lat_alias][content_tag]) {
                                            lat = parseFloat(ParseCoordinate(trkseg[track_point_tag][k][tag_prefix + lat_alias][content_tag]));
                                            lon = parseFloat(ParseCoordinate(trkseg[track_point_tag][k][tag_prefix + lon_alias][content_tag]));
                                        }
                                    } else if (trkseg[track_point_tag][k][tag_prefix + lat_alias]) {
                                        lat = parseFloat(ParseCoordinate(trkseg[track_point_tag][k][tag_prefix + lat_alias]));
                                        lon = parseFloat(ParseCoordinate(trkseg[track_point_tag][k][tag_prefix + lon_alias]));
                                    }
                                    if (!isNaN(lat) && !isNaN(lon) && Math.abs(lat) <= 90 && Math.abs(lon) <= 180) {
                                        lat_sum += lat;
                                        lon_sum += lon;
                                        coord_count += 1;
                                        if (!bounds_w || lon < bounds_w) {
                                            bounds_w = lon;
                                        }
                                        if (!bounds_e || lon > bounds_e) {
                                            bounds_e = lon;
                                        }
                                        if (!bounds_s || lat < bounds_s) {
                                            bounds_s = lat;
                                        }
                                        if (!bounds_n || lat > bounds_n) {
                                            bounds_n = lat;
                                        }
                                        pts.push(new GLatLng(lat, lon));
                                    }
                                }
                                if (pts.length > 0) {
                                    if (track_info_array[tn]['fill_opacity'] && track_info_array[tn]['fill_opacity'] > 0) {
                                        var fill_color = (track_info_array[tn]['fill_color']) ? track_info_array[tn]['fill_color'] : track_info_array[tn]['color'];
                                        track_array[tn].push(new GPolygon(pts, GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['width'], track_info_array[tn]['opacity'], GV_Color_Name2Hex(fill_color), track_info_array[tn]['fill_opacity'], {mouseOutTolerance: 1}));
                                    } else {
                                        track_array[tn].push(new GPolyline(pts, GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['width'], track_info_array[tn]['opacity'], {mouseOutTolerance: 1}));
                                    }
                                    map.addOverlay(track_array[tn][ track_array[tn].length - 1 ]);
                                    add_to_tracklist = true;
                                }
                            }
                        }
                    } else { // trackpoints are directly under the track tag (no segments)
                        if (!this_trk[track_point_tag].length) {
                            this_trk[track_point_tag] = [this_trk[track_point_tag]];
                        }
                        var pts = [];
                        var lat_alias = 'lat';
                        var lon_alias = 'lon';
                        if (this_trk[track_point_tag] && this_trk[track_point_tag].length) {
                            for (var field in this_trk[track_point_tag][0]) { // for efficiency, only search the first point for latitude & longitude tags
                                var field_cropped = field.substring(prefix_length);
                                if (field_cropped.match(/^(lati?|latt?itude)\b/i)) {
                                    lat_alias = field_cropped;
                                }
                                else if (field_cropped.match(/^(long?|lng|long?t?itude)\b/i)) {
                                    lon_alias = field_cropped;
                                }
                            }
                            for (var k = 0; k < this_trk[track_point_tag].length; k++) {
                                var lat = 91;
                                var lon = 181;
                                if (content_tag) {
                                    if (this_trk[track_point_tag][k][tag_prefix + lat_alias] && this_trk[track_point_tag][k][tag_prefix + lat_alias][content_tag]) {
                                        lat = parseFloat(ParseCoordinate(this_trk[track_point_tag][k][tag_prefix + lat_alias][content_tag]));
                                        lon = parseFloat(ParseCoordinate(this_trk[track_point_tag][k][tag_prefix + lon_alias][content_tag]));
                                    }
                                } else if (this_trk[track_point_tag][k][tag_prefix + lat_alias]) {
                                    lat = parseFloat(ParseCoordinate(this_trk[track_point_tag][k][tag_prefix + lat_alias]));
                                    lon = parseFloat(ParseCoordinate(this_trk[track_point_tag][k][tag_prefix + lon_alias]));
                                }
                                if (Math.abs(lat) <= 90 && Math.abs(lon) <= 180) {
                                    pts.push(new GLatLng(lat, lon));
                                    lat_sum += lat;
                                    lon_sum += lon;
                                    coord_count += 1;
                                }
                            }
                            if (pts.length) {
                                if (track_info_array[tn]['fill_opacity'] > 0) {
                                    track_array[tn].push(new GPolygon(pts, GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['width'], track_info_array[tn]['opacity'], GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['fill_opacity'], {mouseOutTolerance: 1}));
                                } else {
                                    track_array[tn].push(new GPolyline(pts, GV_Color_Name2Hex(track_info_array[tn]['color']), track_info_array[tn]['width'], track_info_array[tn]['opacity'], {mouseOutTolerance: 1}));
                                }
                                map.addOverlay(track_array[tn][ track_array[tn].length - 1 ]);
                                add_to_tracklist = true;
                            }
                        }
                    }
                    if (coord_count) {
                        track_info_array[tn]['center'] = [];
                        track_info_array[tn]['center']['lat'] = lat_sum / coord_count;
                        track_info_array[tn]['center']['lon'] = lon_sum / coord_count;
                    }
                    if (bounds_s != null && bounds_n != null && bounds_w != null && bounds_e != null) {
                        track_info_array[tn]['bounds'] = [];
                        track_info_array[tn]['bounds']['s'] = bounds_s;
                        track_info_array[tn]['bounds']['n'] = bounds_n;
                        track_info_array[tn]['bounds']['w'] = bounds_w;
                        track_info_array[tn]['bounds']['e'] = bounds_e;
                    }
                    trk = track_array;
                    trk_info = track_info_array;
                    GV_Finish_Track(tn);
                    if (add_to_tracklist && !track_info_array[tn]['no_list']) {
                        GV_Add_Track_to_Tracklist({bullet: '- ', name: track_info_array[tn]['name'], desc: track_info_array[tn]['desc'], color: track_info_array[tn]['color'], map_name: map_name, id: track_array_name + "[" + tn + "]"});
                    }
                }
            }
        }

        if (data && data[root_tag] && data[root_tag][marker_tag]) {
            var alias = [];
            var numeric = {lat: true, lon: true, scale: true, opacity: true, rotation: true};
            if (!data[root_tag][marker_tag].length) { // if there's only one, it has no length; make it into an array
                data[root_tag][marker_tag] = [data[root_tag][marker_tag]];
            }
            var row1 = data[root_tag][marker_tag][0];
            // This is potentially problematic: only the FIRST marker (row) is scanned for non-standard field names.  Keep an eye on that.
            // However, note that with Google Spreadsheets, the feed doesn't even WORK if it's not row 1:header, row2:data!
            for (var tag in row1) {
                if (tag_prefix == '' || tag.indexOf(tag_prefix) == 0) {
                    var field = tag.substring(prefix_length);
                    if (field.match(/^(name|nom|naam)\d?\b/i)) {
                        alias[field] = 'name';
                    }
                    else if (field.match(/^(desc|descr|description)\d?\b/i)) {
                        alias[field] = 'desc';
                    }
                    else if (field.match(/^(url|web.?page|link)\d?\b/i)) {
                        alias[field] = 'url';
                    }
                    else if (field.match(/^(lati?|latt?itude)\b/i)) {
                        alias[field] = 'lat';
                    }
                    else if (field.match(/^(long?|lng|long?t?itude)\b/i)) {
                        alias[field] = 'lon';
                    }
                    else if (field.match(/^(colou?re?|couleur)\b/i)) {
                        alias[field] = 'color';
                    }
                    else if (field.match(/^(icon|sym|symbol).?size\b/i)) {
                        alias[field] = 'icon_size';
                    }
                    else if (field.match(/^(icon|sym|symbol).?anchor\b/i)) {
                        alias[field] = 'icon_anchor';
                    }
                    else if (field.match(/^(icon|sym|symbol).?scale\b/i)) {
                        alias[field] = 'scale';
                    }
                    else if (field.match(/^(icon|sym|symbol).?opacity\b/i)) {
                        alias[field] = 'scale';
                    }
                    else if (field.match(/^(icon|sym|symbol)\b/i)) {
                        alias[field] = 'icon';
                    }
                    else if (field.match(/^(thumbnail|thumb|tn).?(width|size)\b/i)) {
                        alias[field] = 'thumbnail_width';
                    }
                    else if (field.match(/^(thumbnail|thumb|tn)\b/i)) {
                        alias[field] = 'thumbnail';
                    }
                    else if (field.match(/^(opaque|opacity)\b/i)) {
                        alias[field] = 'opacity';
                    }
                    else if (field.match(/^label.?offset\b/i)) {
                        alias[field] = 'label_offset';
                    }
                    else if (field.match(/^zoom.?level\b/i)) {
                        alias[field] = 'zoom_level';
                    }
                    else if (field.match(/^gv.?marker.?options\b/i)) {
                        alias[field] = 'gv_marker_options';
                    }
                    else if (field.match(/^(gv.?)?track.?number\b/i)) {
                        alias[field] = 'gv_track_number';
                    }
                }
            }
            // A few extra "universal" aliases that might not be in the first row:
            alias['description'] = 'desc';
            alias['sym'] = 'icon';
            alias['symbol'] = 'icon';
            alias['colour'] = 'color';
            alias['link'] = 'url';
            alias['latitude'] = 'lat';
            alias['longitude'] = 'lon';
            alias['long'] = 'lon';
            alias['lng'] = 'lon';
            if (opts['field_alias']) {
                for (var field in opts['field_alias']) {
                    alias[opts['field_alias'][field]] = field; // note that this eliminates the former column.  I.e., if alias['folder'] == 'icon', then icon ceases to exist. Therefore synthesize_fields may be preferable.
                }
            }
            var marker_count = 0;
            var marker_start_index = 0;
            var marker_end_index = data[root_tag][marker_tag].length;
            if (opts['first']) {
                if (parseInt(opts['first']) < data[root_tag][marker_tag].length) {
                    marker_end_index = parseInt(opts['first']);
                }
            } else if (opts['last']) {
                if (parseInt(opts['last']) < data[root_tag][marker_tag].length) {
                    marker_start_index = data[root_tag][marker_tag].length - parseInt(opts['last'])
                }
            }

            for (var i = marker_start_index; i < marker_end_index; i++) {
                var row = data[root_tag][marker_tag][i];
                var marker_data = [];
                for (var tag in row) {
                    if (tag.indexOf(tag_prefix) == 0) {
                        var field = tag.substring(prefix_length);
                        var original_field = field;
                        field = (alias[field]) ? alias[field].toLowerCase() : field.toLowerCase();
                        var value = (content_tag) ? row[tag][content_tag] : row[tag];
                        if (tag == 'link' && row['link']['href']) {
                            value = row['link']['href'];
                        } // Garmin's <link> tag uses an href attribute to hold the URL
                        if (value != null && typeof(value) != 'object' && value.toString().match(/\S/)) {
                            // special processing of certain fields
                            if (field == 'georss:point' || field == 'georss_point') {
                                var coordinates = value.split(/[^0-9\.\-]/);
                                if (coordinates[0] && coordinates[1]) {
                                    marker_data['lat'] = parseFloat(coordinates[0]);
                                    marker_data['lon'] = parseFloat(coordinates[1]);
                                }
                            } else if (field == 'lat' || field == 'lon') {
                                marker_data[field] = parseFloat(ParseCoordinate(value));
                            } else if (field == 'gv_marker_options') {
                                try {
                                    eval('var extra_marker_list_options = {' + value + '};');
                                    for (var op in extra_marker_list_options) {
                                        marker_data[op] = extra_marker_list_options[op];
                                    }
                                } catch (error) {
                                }
                            } else if (field == 'icon_size' || field == 'icon_anchor' || field == 'label_offset' || field == 'thumbnail_size') {
                                var numbers = FindOneOrTwoNumbersInString(value);
                                if (numbers.length == 1) {
                                    marker_data[field] = [numbers[0], numbers[0]];
                                }
                                else if (numbers.length == 2) {
                                    marker_data[field] = [numbers[0], numbers[1]];
                                }
                            } else if (opts['time_stamp'] && opts['time_stamp'].toLowerCase() == field && value && value.toString().match(/^(\d+\D\d+\D\d+|\d\d\d\d\d\d\d\d\d\d)/)) {
                                marker_data[field] = GV_Format_Time(value, opts['time_zone'], opts['time_zone_text'], opts['time_12hour']);
                            } else {
                                if (numeric[field] || parseFloat(value).toString() == value.toString()) {
                                    marker_data[field] = parseFloat(value);
                                } else if (value.toLowerCase() == 'true') {
                                    marker_data[field] = true;
                                } else if (value.toLowerCase() == 'false') {
                                    marker_data[field] = false;
                                } else if (value == undefined) {
                                    marker_data[field] = '';
                                } else {
                                    marker_data[field] = value;
                                }
                            }
                        } else {
                            marker_data[field] = null;
                        }
                        if (original_field != field && !marker_data[original_field]) {
                            marker_data[original_field] = marker_data[field];
                        } // for field synthesis using original column names
                    }
                }
                if (marker_data['track_number'] && !marker_data['color'] && track_info_array && track_info_array[marker_data['track_number']] && track_info_array[marker_data['track_number']]['color']) {
                    marker_data['color'] = track_info_array[marker_data['track_number']]['color'];
                }
                if (marker_data['icon'] && marker_data['icon'].match(/google|gstatic/)) {
                    marker_data = GV_KML_Icon_Anchors(marker_data);
                }
                if (marker_data['icon'] == 'tickmark' && typeof(marker_data['course']) != 'undefined' && (marker_data['rotation'] == '' || typeof(marker_data['rotation']) != 'undefined')) {
                    marker_data['rotation'] = parseFloat(marker_data['course']);
                    marker_data['type'] = 'tickmark';
                }
                if (opts['synthesize_fields']) {
                    for (var f in opts['synthesize_fields']) {
                        if (opts['synthesize_fields'][f]) {
                            var template = opts['synthesize_fields'][f];
                            template = template.replace(synthesize_pattern,
                                    function(complete_match, field_name) {
                                        field_name = (tagnames_stripped) ? field_name.replace(/[^A-Za-z0-9\.]/gi, '').toLowerCase() : field_name.toLowerCase();
                                        if (marker_data[field_name] || marker_data[field_name] == '0' || marker_data[field_name] === false) {
                                            return (marker_data[field_name]);
                                        } else if (marker_data[alias[field_name]] || marker_data[alias[field_name]] == '0' || marker_data[alias[field_name]] === false) {
                                            return (marker_data[alias[field_name]]);
                                        } else {
                                            return ('');
                                        }
                                    }
                            );
                            marker_data[f] = template;
                        }
                    }
                }
                if (opts['google_content_as_desc']) {
                    marker_data['desc'] = (content_tag) ? row['content'][content_tag] : row['content'];
                }
                if (opts['ignore_styles']) {
                    marker_data['color'] = null;
                    marker_data['icon'] = null;
                    marker_data['icon_size'] = null;
                    marker_data['icon_anchor'] = null;
                    marker_data['scale'] = null;
                    marker_data['opacity'] = null;
                    marker_data['rotation'] = null;
                }
                var marker_ok = true;
                if (isNaN(marker_data['lat']) || isNaN(marker_data['lon']) || (marker_data['lat'] == 0 && marker_data['lon'] == 0) || Math.abs(marker_data['lat']) > 90 || Math.abs(marker_data['lon']) > 180 || marker_data['lat'] == undefined || marker_data['lon'] == undefined) {
                    marker_ok = false;
                } else if (filter_regexp && filter_field) {
                    if (marker_data[filter_field] && marker_data[filter_field].toString().match(filter_regexp)) {
                        marker_ok = true;
                    } else {
                        marker_ok = false;
                    }
                }
                if (marker_ok) {
                    if (opts['default_marker']) {
                        for (mdf = 0; mdf < marker_default_fields.length; mdf++) {
                            if (!marker_data[marker_default_fields[mdf]] && opts['default_marker'][marker_default_fields[mdf]]) {
                                marker_data[marker_default_fields[mdf]] = opts['default_marker'][marker_default_fields[mdf]];
                            }
                        }
                    }
                    marker_data['dynamic'] = gv_dynamic_file_index + 1; // +1 so we can test for its presence!
                    marker_array.push(GV_Marker(map, marker_data));
                    marker_count++;
                    if (marker_data['gv_track_number'] && track_array && track_array[ marker_data['gv_track_number'] ]) {
                        track_array[ marker_data['gv_track_number'] ].push(marker_array[ marker_array.length - 1 ]);
                    }
                }
            }
        }
    } // end "else" where the "if" was "is this a KML file?

    gv_options['zoom'] = (opts['autozoom'] != false && !opts['reload_on_move'] && !opts['reload_on_request']) ? 'auto' : gv_options['zoom'];
    gv_options['autozoom_adjustment'] = (opts['zoom_adjustment']) ? opts['zoom_adjustment'] : 0;
    gv_options['autozoom_default'] = (opts['zoom_default']) ? opts['zoom_default'] : 8;
    // if (marker_count == 1 && $('gv_crosshair')) { $('gv_crosshair').style.display = 'none'; }
    if (gv_options['dynamic_data']) {
        gv_options['dynamic_data'][gv_dynamic_file_index]['processed'] = true;
    }
    GV_Finish_Map(gv_options);
}


function FindGoogleAPIVersion() {
    var v = 2;
    var scripts = document.getElementsByTagName("script");
    for (var i = 0; i < scripts.length; i++) {
        var pattern = /\/mapfiles\/([0-9]+)\/maps([0-9])?\.api\//;
        var m = pattern.exec(scripts[i].src);
        if (m != null && m[1] && m[2]) {
            v = parseFloat(m[2] + '.' + m[1]);
            break;
        }
    }
    return v;
}

function ParseCoordinate(coordinate) {
    if (coordinate == null) {
        return '';
    }
    coordinate = coordinate.toString();
    coordinate = coordinate.replace(/([0-9][0-9][0-9]?)([0-9][0-9])\.([0-9]+)/, '$1 $2'); // convert DDMM.MM format to DD MM.MM
    coordinate = coordinate.replace(/[^NESW0-9\.\- ]/gi, ' '); // only a few characters are useful; delete the rest
    var neg = 0;
    if (coordinate.match(/(^\s*-|[WS])/i)) {
        neg = 1;
    }
    coordinate = coordinate.replace(/[NESW\-]/gi, ' ');
    if (!coordinate.match(/[0-9]/i)) {
        return '';
    }
    parts = coordinate.match(/([0-9\.\-]+)[^0-9\.]*([0-9\.]+)?[^0-9\.]*([0-9\.]+)?/);
    if (!parts || parts[1] == null) {
        return '';
    } else {
        n = parseFloat(parts[1]);
        if (parts[2]) {
            n = n + parseFloat(parts[2]) / 60;
        }
        if (parts[3]) {
            n = n + parseFloat(parts[3]) / 3600;
        }
        if (neg && n >= 0) {
            n = 0 - n;
        }
        n = Math.round(10000000 * n) / 10000000;
        if (n == Math.floor(n)) {
            n = n + '.0';
        }
        n = n + ''; // force number into a string context
        n = n.replace(/,/g, '.'); // in case some foreign systems created a number with a comma in it
        return n;
    }
}
function FindOneOrTwoNumbersInString(text) {
    var two_number_pattern = new RegExp('(-?[0-9]+\.?[0-9]*)[^0-9-]+(-?[0-9]+\.?[0-9]*)');
    var one_number_pattern = new RegExp('(-?[0-9]+\.?[0-9]*)');
    var output = [];
    if (text && text.match(two_number_pattern)) {
        var two_number_match = two_number_pattern.exec(text);
        if (two_number_match) {
            output = [parseFloat(two_number_match[1]), parseFloat(two_number_match[2])];
        }
    } else if (text && text.match(one_number_pattern)) {
        var one_number_match = one_number_pattern.exec(text);
        if (one_number_match) {
            output = [parseFloat(one_number_match[1])];
        }
    }
    return output;
}


/**************************************************
 elabel.js
 (adapted from http://www.econym.demon.co.uk/googlemaps/elabel.htm)
 (My modification: adding the "label_id", "hide", "centered", "left", and "style" parameters)
 **************************************************/

function ELabel(point, html, classname, iconSize, pixelOffset, percentOpacity, overlap, label_id, hide, centered, left, style) {
    // Mandatory parameters
    this.point = point;
    this.html = html;

    // Optional parameters
    this.classname = classname || "";
    this.styledef = style || "";
    this.iconSize = iconSize || new GSize(0, 0);
    this.pixelOffset = pixelOffset || new GSize(0, 0);
    if (percentOpacity) {
        if (percentOpacity < 0) {
            percentOpacity = 0;
        }
        if (percentOpacity > 100) {
            percentOpacity = 100;
        }
    }
    this.percentOpacity = percentOpacity;
    this.overlap = overlap || false;
    this.label_id = label_id;
    this.hide = hide;
    this.centered = centered;
    this.left = left;
}

if (google_api_version >= 2) {
    ELabel.prototype = new GOverlay();

    ELabel.prototype.initialize = function(map) {
        var div = document.createElement("div");
        div.style.position = "absolute";
        div.style.visibility = (this.hide) ? 'hidden' : 'visible';
        div.innerHTML = '<div id = "' + this.label_id + '" class="' + this.classname + '" style="' + this.styledef + '">' + this.html + '</div>';
        map.getPane(G_MAP_FLOAT_SHADOW_PANE).appendChild(div);
        this.map_ = map;
        this.div_ = div;
        if (this.percentOpacity) {
            if (typeof(div.style.filter) == 'string') {
                div.style.filter = 'alpha(opacity:' + this.percentOpacity + ')';
            }
            if (typeof(div.style.KHTMLOpacity) == 'string') {
                div.style.KHTMLOpacity = this.percentOpacity / 100;
            }
            if (typeof(div.style.MozOpacity) == 'string') {
                div.style.MozOpacity = this.percentOpacity / 100;
            }
            if (typeof(div.style.opacity) == 'string') {
                div.style.opacity = this.percentOpacity / 100;
            }
        }
        if (this.overlap) {
            var z = GOverlay.getZIndex(this.point.lat());
            this.div_.style.zIndex = z;
        }
    }

    ELabel.prototype.remove = function() {
        this.div_.parentNode.removeChild(this.div_);
    }

    ELabel.prototype.copy = function() {
        return new ELabel(this.point, this.html, this.classname, this.pixelOffset, this.percentOpacity, this.overlap);
    }

    ELabel.prototype.redraw = function(force) {
        var p = this.map_.fromLatLngToDivPixel(this.point);
        // var h = parseInt(this.div_.clientHeight); // for some reason, this is problematic when custom font sizes are applied!
        var h = 15; // ...so let's just make it a fixed size
        var x_offset = this.pixelOffset.width;
        x_offset += (this.centered) ? 0 - parseInt(this.div_.clientWidth / 2) : (this.left) ? 0 - this.iconSize.width - 1 - this.div_.clientWidth : this.iconSize.width + 1;
        var y_offset = this.pixelOffset.height;
        y_offset += (this.centered) ? this.iconSize.height + 1 : 0 - parseInt(h / 2);
        this.div_.style.left = (p.x + x_offset) + "px";
        this.div_.style.top = (p.y + y_offset) + "px";
    }


    ELabel.prototype.show = function() {
        this.div_.style.display = "";
    }

    ELabel.prototype.hide = function() {
        this.div_.style.display = "none";
    }

    ELabel.prototype.setContents = function(html) {
        this.html = html;
        this.div_.innerHTML = '<div id = "' + this.label_id + '" class="' + this.classname + '">' + this.html + '</div>';
        this.redraw(true);
    }

    ELabel.prototype.setPoint = function(point) {
        this.point = point;
        if (this.overlap) {
            var z = GOverlay.getZIndex(this.point.lat());
            this.div_.style.zIndex = z;
        }
        this.redraw(true);
    }

    ELabel.prototype.setOpacity = function(percentOpacity) {
        if (percentOpacity) {
            if (percentOpacity < 0) {
                percentOpacity = 0;
            }
            if (percentOpacity > 100) {
                percentOpacity = 100;
            }
        }
        this.percentOpacity = percentOpacity;
        if (this.percentOpacity) {
            if (typeof(this.div_.style.filter) == 'string') {
                this.div_.style.filter = 'alpha(opacity:' + this.percentOpacity + ')';
            }
            if (typeof(this.div_.style.KHTMLOpacity) == 'string') {
                this.div_.style.KHTMLOpacity = this.percentOpacity / 100;
            }
            if (typeof(this.div_.style.MozOpacity) == 'string') {
                this.div_.style.MozOpacity = this.percentOpacity / 100;
            }
            if (typeof(this.div_.style.opacity) == 'string') {
                this.div_.style.opacity = this.percentOpacity / 100;
            }
        }
    }
}

/**************************************************
 * dom-drag.js
 * 09.25.2001
 * www.youngpup.net
 * Script featured on Dynamic Drive (http://www.dynamicdrive.com) 12.08.2005
 **************************************************
 * 10.28.2001 - fixed minor bug where events
 * sometimes fired off the handle, not the root.
 **************************************************/

var GV_Drag = {
    obj: null,
    init: function(o, oRoot, minX, maxX, minY, maxY, bSwapHorzRef, bSwapVertRef, fXMapper, fYMapper)
    {
        if (!o) {
            return false;
        } // CUSTOM ADDITION

        o.onmousedown = GV_Drag.start;

        o.hmode = bSwapHorzRef ? false : true;
        o.vmode = bSwapVertRef ? false : true;

        o.root = oRoot && oRoot != null ? oRoot : o;

        if (o.hmode && isNaN(parseInt(o.root.style.left)))
            o.root.style.left = "0px";
        if (o.vmode && isNaN(parseInt(o.root.style.top)))
            o.root.style.top = "0px";
        if (!o.hmode && isNaN(parseInt(o.root.style.right)))
            o.root.style.right = "0px";
        if (!o.vmode && isNaN(parseInt(o.root.style.bottom)))
            o.root.style.bottom = "0px";

        o.minX = typeof minX != 'undefined' ? minX : null;
        o.minY = typeof minY != 'undefined' ? minY : null;
        o.maxX = typeof maxX != 'undefined' ? maxX : null;
        o.maxY = typeof maxY != 'undefined' ? maxY : null;

        o.xMapper = fXMapper ? fXMapper : null;
        o.yMapper = fYMapper ? fYMapper : null;

        o.root.onDragStart = new Function();
        o.root.onDragEnd = new Function();
        o.root.onDrag = new Function();
    },
    start: function(e)
    {
        var o = GV_Drag.obj = this;
        e = GV_Drag.fixE(e);
        var y = parseInt(o.vmode ? o.root.style.top : o.root.style.bottom);
        var x = parseInt(o.hmode ? o.root.style.left : o.root.style.right);
        o.root.onDragStart(x, y);

        o.lastMouseX = e.clientX;
        o.lastMouseY = e.clientY;

        if (o.hmode) {
            if (o.minX != null)
                o.minMouseX = e.clientX - x + o.minX;
            if (o.maxX != null)
                o.maxMouseX = o.minMouseX + o.maxX - o.minX;
        } else {
            if (o.minX != null)
                o.maxMouseX = -o.minX + e.clientX + x;
            if (o.maxX != null)
                o.minMouseX = -o.maxX + e.clientX + x;
        }

        if (o.vmode) {
            if (o.minY != null)
                o.minMouseY = e.clientY - y + o.minY;
            if (o.maxY != null)
                o.maxMouseY = o.minMouseY + o.maxY - o.minY;
        } else {
            if (o.minY != null)
                o.maxMouseY = -o.minY + e.clientY + y;
            if (o.maxY != null)
                o.minMouseY = -o.maxY + e.clientY + y;
        }

        document.onmousemove = GV_Drag.drag;
        document.onmouseup = GV_Drag.end;

        return false;
    },
    drag: function(e)
    {
        e = GV_Drag.fixE(e);
        var o = GV_Drag.obj;

        var ey = e.clientY;
        var ex = e.clientX;
        var y = parseInt(o.vmode ? o.root.style.top : o.root.style.bottom);
        var x = parseInt(o.hmode ? o.root.style.left : o.root.style.right);
        var nx, ny;

        if (o.minX != null)
            ex = o.hmode ? Math.max(ex, o.minMouseX) : Math.min(ex, o.maxMouseX);
        if (o.maxX != null)
            ex = o.hmode ? Math.min(ex, o.maxMouseX) : Math.max(ex, o.minMouseX);
        if (o.minY != null)
            ey = o.vmode ? Math.max(ey, o.minMouseY) : Math.min(ey, o.maxMouseY);
        if (o.maxY != null)
            ey = o.vmode ? Math.min(ey, o.maxMouseY) : Math.max(ey, o.minMouseY);

        nx = x + ((ex - o.lastMouseX) * (o.hmode ? 1 : -1));
        ny = y + ((ey - o.lastMouseY) * (o.vmode ? 1 : -1));

        if (o.xMapper)
            nx = o.xMapper(y)
        else if (o.yMapper)
            ny = o.yMapper(x)

        GV_Drag.obj.root.style[o.hmode ? "left" : "right"] = nx + "px";
        GV_Drag.obj.root.style[o.vmode ? "top" : "bottom"] = ny + "px";
        GV_Drag.obj.lastMouseX = ex;
        GV_Drag.obj.lastMouseY = ey;

        GV_Drag.obj.root.onDrag(nx, ny);
        return false;
    },
    end: function()
    {
        document.onmousemove = null;
        document.onmouseup = null;
        GV_Drag.obj.root.onDragEnd(parseInt(GV_Drag.obj.root.style[GV_Drag.obj.hmode ? "left" : "right"]),
                parseInt(GV_Drag.obj.root.style[GV_Drag.obj.vmode ? "top" : "bottom"]));
        GV_Drag.obj = null;
    },
    fixE: function(e)
    {
        if (typeof e == 'undefined')
            e = window.event;
        if (typeof e.layerX == 'undefined')
            e.layerX = e.offsetX;
        if (typeof e.layerY == 'undefined')
            e.layerY = e.offsetY;
        return e;
    }
};

var Drag = GV_Drag; // backwards compatibility


/*******************************************************************
 * JSON functions from Jason Leavitt
 *******************************************************************
 * GPS Visualizer customiztion: added the "clean" parameter to 
 * JSONscriptRequest so the "noCacheIE" argument is NOT sent.
 * (Extra parameters break Google Docs.)
 *
 * Also added a "uri_escape" function
 *
 * Also added a fix for IE6- when there is a self-closing "base" tag
 *******************************************************************/

// JSONscriptRequest -- a simple class for accessing Yahoo! Web Services
// using dynamically generated script tags and JSON
//
// Author: Jason Levitt
// Date: December 7th, 2005
//
// Constructor -- pass a REST request URL to the constructor
//

function JSONscriptRequest(fullUrl, opts) {
    var clean = (opts && opts['clean']) ? true : false;
    // REST request path
    this.fullUrl = fullUrl;
    // Keep IE from caching requests
    this.noCacheIE = (clean) ? '' : (fullUrl.indexOf('?') > -1) ? '&noCacheIE=' + (new Date()).getTime() : '?noCacheIE=' + (new Date()).getTime();
    // Get the DOM location to put the script tag
    this.headLoc = document.getElementsByTagName("head").item(0);
    // Generate a unique script tag id
    this.scriptId = 'YJscriptId' + JSONscriptRequest.scriptCounter++;
}

// Static script ID counter
JSONscriptRequest.scriptCounter = 1;

// buildScriptTag method
//
JSONscriptRequest.prototype.buildScriptTag = function() {

    // Create the script tag
    this.scriptObj = document.createElement("script");

    // Add script object attributes
    this.scriptObj.setAttribute("type", "text/javascript");
    this.scriptObj.setAttribute("src", (this.fullUrl.match(/&callback=/)) ? this.fullUrl.replace(/&callback=/, this.noCacheIE + '&callback=') : this.fullUrl + this.noCacheIE); // Google requires 'callback' to be the LAST parameter
    this.scriptObj.setAttribute("id", this.scriptId);
}

// buildScriptTag2 method
//
JSONscriptRequest.prototype.buildScriptTag_custom = function(js) {

    // Create the script tag
    this.scriptObj = document.createElement("script");

    // Add script object attributes
    this.scriptObj.setAttribute("type", "text/javascript");
    this.scriptObj.setAttribute("id", this.scriptId);
    this.scriptObj.text = js; // it should really be .innerHTML, not .text, but IE demands .text
}

// removeScriptTag method
// 
JSONscriptRequest.prototype.removeScriptTag = function() {
    // Destroy the script tag
    if (this.scriptObj) {
        if (this.scriptObj.parentNode != this.headLoc) { // IE doesn't understand self-closing tags!
            // eval("a"+"lert ('Internet Explorer 6 is unbelievably stupid!')");
            this.scriptObj.parentNode.removeChild(this.scriptObj); // although, maybe this makes more sense anyway?
        } else {
            this.headLoc.removeChild(this.scriptObj);
        }
    }
}

// addScriptTag method
//
JSONscriptRequest.prototype.addScriptTag = function() {
    // Create the script tag
    this.headLoc.appendChild(this.scriptObj);
}

function uri_escape(text) {
    text = escape(text);
    text = text.replace(/\//g, "%2F");
    text = text.replace(/\?/g, "%3F");
    text = text.replace(/=/g, "%3D");
    text = text.replace(/&/g, "%26");
    text = text.replace(/@/g, "%40");
    text = text.replace(/\#/g, "%23");
    return (text);
}
function uri_unescape(text) {
    text = text.replace(/\+/g, ' ');
    text = unescape(text);
    return (text);
}

/*	This work is licensed under Creative Commons GNU LGPL License.
 License: http://creativecommons.org/licenses/LGPL/2.1/
 Version: 0.9
 Author:  Stefan Goessner/2006
 Web:	 http://goessner.net/ 
 */
function json2xml(o, tab) {
    var toXml = function(v, name, ind) {
        var xml = "";
        if (v instanceof Array) {
            for (var i = 0, n = v.length; i < n; i++)
                xml += ind + toXml(v[i], name, ind + "\t") + "\n";
        }
        else if (typeof(v) == "object") {
            var hasChild = false;
            xml += ind + "<" + name;
            for (var m in v) {
                if (m.charAt(0) == "@")
                    xml += " " + m.substr(1) + "=\"" + v[m].toString() + "\"";
                else
                    hasChild = true;
            }
            xml += hasChild ? ">" : "/>";
            if (hasChild) {
                for (var m in v) {
                    if (m == "#text")
                        xml += v[m];
                    else if (m == "#cdata")
                        xml += "<![CDATA[" + v[m] + "]]>";
                    else if (m.charAt(0) != "@")
                        xml += toXml(v[m], m, ind + "\t");
                }
                xml += (xml.charAt(xml.length - 1) == "\n" ? ind : "") + "</" + name + ">";
            }
        }
        else {
            xml += ind + "<" + name + ">" + v.toString() + "</" + name + ">";
        }
        return xml;
    }, xml = "";
    for (var m in o)
        xml += toXml(o[m], m, "");
    return tab ? xml.replace(/\t/g, tab) : xml.replace(/\t|\n/g, "");
}

/*	This work is licensed under Creative Commons GNU LGPL License.
 
 License: http://creativecommons.org/licenses/LGPL/2.1/
 Version: 0.9
 Author:  Stefan Goessner/2006
 Web:	  http://goessner.net/ 
 
 ### GV CUSTOMIZATION: added "attribute_prefix" option; it was hard-coded as "@"
 ### GV CUSTOMIZATION: removed all '#cdata' and '#text' tags
 */
function xml2json(xml, tab, attribute_prefix) {
    var X = {
        toObj: function(xml) {
            var o = {};
            if (xml.nodeType == 1) {	// element node ..
                if (xml.attributes.length)	// element with attributes  ..
                    for (var i = 0; i < xml.attributes.length; i++)
                        o[attribute_prefix + xml.attributes[i].nodeName] = (xml.attributes[i].nodeValue || "").toString();
                if (xml.firstChild) { // element has child nodes ..
                    var textChild = 0, cdataChild = 0, hasElementChild = false;
                    for (var n = xml.firstChild; n; n = n.nextSibling) {
                        if (n.nodeType == 1)
                            hasElementChild = true;
                        else if (n.nodeType == 3 && n.nodeValue.match(/[^ \f\n\r\t\v]/))
                            textChild++; // non-whitespace text
                        else if (n.nodeType == 4)
                            cdataChild++; // cdata section node
                    }
                    if (hasElementChild) {
                        if (textChild < 2 && cdataChild < 2) { // structured element with evtl. a single text or/and cdata node ..
                            X.removeWhite(xml);
                            for (var n = xml.firstChild; n; n = n.nextSibling) {
                                if (n.nodeType == 3)  // text node
                                    o = X.escape(n.nodeValue); // CUSTOMIZATION
                                // o["#text"] = X.escape(n.nodeValue);
                                else if (n.nodeType == 4)  // cdata node
                                    o = X.escape(n.nodeValue); // CUSTOMIZATION
                                // o["#cdata"] = X.escape(n.nodeValue);
                                else if (o[n.nodeName]) {  // multiple occurence of element ..
                                    if (o[n.nodeName] instanceof Array)
                                        o[n.nodeName][o[n.nodeName].length] = X.toObj(n);
                                    else
                                        o[n.nodeName] = [o[n.nodeName], X.toObj(n)];
                                }
                                else  // first occurence of element..
                                    o[n.nodeName] = X.toObj(n);
                            }
                        }
                        else { // mixed content
                            if (!xml.attributes.length)
                                o = X.escape(X.innerXml(xml));
                            else
                                o = X.escape(X.innerXml(xml)); // CUSTOMIZATION
                            // o["#text"] = X.escape(X.innerXml(xml));
                        }
                    }
                    else if (textChild) { // pure text
                        if (!xml.attributes.length)
                            o = X.escape(X.innerXml(xml));
                        else
                            o = X.escape(X.innerXml(xml)); // CUSTOMIZATION
                        // o["#text"] = X.escape(X.innerXml(xml));
                    }
                    else if (cdataChild) { // cdata
                        if (cdataChild > 1)
                            o = X.escape(X.innerXml(xml));
                        else
                            for (var n = xml.firstChild; n; n = n.nextSibling) {
                                o = X.escape(n.nodeValue);
                            } // CUSTOMIZATION
                        // for (var n=xml.firstChild; n; n=n.nextSibling) { o["#cdata"] = X.escape(n.nodeValue); }
                    }
                }
                if (!xml.attributes.length && !xml.firstChild)
                    o = null;
            }
            else if (xml.nodeType == 9) { // document.node
                o = X.toObj(xml.documentElement);
            }
            else
                var xlert = ("unhandled node type: " + xml.nodeType);
            return o;
        },
        toJson: function(o, name, ind) {
            var json = name ? ("\"" + name + "\"") : "";
            if (o instanceof Array) {
                for (var i = 0, n = o.length; i < n; i++)
                    o[i] = X.toJson(o[i], "", ind + "\t");
                json += (name ? ":[" : "[") + (o.length > 1 ? ("\n" + ind + "\t" + o.join(",\n" + ind + "\t") + "\n" + ind) : o.join("")) + "]";
            }
            else if (o == null)
                json += (name && ":") + "null";
            else if (typeof(o) == "object") {
                var arr = [];
                for (var m in o)
                    arr[arr.length] = X.toJson(o[m], m, ind + "\t");
                json += (name ? ":{" : "{") + (arr.length > 1 ? ("\n" + ind + "\t" + arr.join(",\n" + ind + "\t") + "\n" + ind) : arr.join("")) + "}";
            }
            else if (typeof(o) == "string")
                json += (name && ":") + "\"" + o.toString() + "\"";
            else
                json += (name && ":") + o.toString();
            return json;
        },
        innerXml: function(node) {
            var s = ""
            if ("innerHTML" in node)
                s = node.innerHTML;
            else {
                var asXml = function(n) {
                    var s = "";
                    if (n.nodeType == 1) {
                        s += "<" + n.nodeName;
                        for (var i = 0; i < n.attributes.length; i++)
                            s += " " + n.attributes[i].nodeName + "=\"" + (n.attributes[i].nodeValue || "").toString() + "\"";
                        if (n.firstChild) {
                            s += ">";
                            for (var c = n.firstChild; c; c = c.nextSibling)
                                s += asXml(c);
                            s += "</" + n.nodeName + ">";
                        }
                        else
                            s += "/>";
                    }
                    else if (n.nodeType == 3)
                        s += n.nodeValue;
                    else if (n.nodeType == 4)
                        s += "<![CDATA[" + n.nodeValue + "]]>";
                    return s;
                };
                for (var c = node.firstChild; c; c = c.nextSibling)
                    s += asXml(c);
            }
            return s;
        },
        escape: function(txt) {
            return txt.replace(/[\\]/g, "\\\\")
                    .replace(/[\"]/g, '\\"')
                    .replace(/[\n]/g, '\\n')
                    .replace(/[\r]/g, '\\r');
        },
        removeWhite: function(e) {
            e.normalize();
            for (var n = e.firstChild; n; ) {
                if (n.nodeType == 3) {  // text node
                    if (!n.nodeValue.match(/[^ \f\n\r\t\v]/)) { // pure whitespace text node
                        var nxt = n.nextSibling;
                        e.removeChild(n);
                        n = nxt;
                    }
                    else
                        n = n.nextSibling;
                }
                else if (n.nodeType == 1) {  // element node
                    X.removeWhite(n);
                    n = n.nextSibling;
                }
                else							 // any other node
                    n = n.nextSibling;
            }
            return e;
        }
    };
    if (xml.nodeType == 9) // document node
        xml = xml.documentElement;
    // var json = X.toJson(X.toObj(X.removeWhite(xml)), xml.nodeName, "\t");
    var json = (xml) ? X.toJson(X.toObj(X.removeWhite(xml)), xml.nodeName, "\t") : '';
    return "{\n" + tab + (tab ? json.replace(/\t/g, tab) : json.replace(/\t|\n/g, "")) + "\n}";
}

// from http://goessner.net/download/prj/jsonxml/ :
function parseXML(xml) {
    var dom = null;
    if (window.DOMParser) {
        try {
            dom = (new DOMParser()).parseFromString(xml, "text/xml");
        }
        catch (e) {
            dom = null;
        }
    } else if (window.ActiveXObject) {
        try {
            dom = new ActiveXObject('Microsoft.XMLDOM');
            dom.async = false;
            if (!dom.loadXML(xml)) // parse error ..
                var xlert = (dom.parseError.reason + dom.parseError.srcText);
        }
        catch (e) {
            dom = null;
        }
    } else {
        return false;
    }
    return dom;
}

// from http://en.wikipedia.org/wiki/XMLHttpRequest : 
function getURL(url, vars, callbackFunction) {
    if (typeof(XMLHttpRequest) == "undefined") {
        XMLHttpRequest = function() {
            try {
                return new ActiveXObject("Msxml2.XMLHTTP.6.0");
            } catch (e) {
            }
            ;
            try {
                return new ActiveXObject("Msxml2.XMLHTTP.3.0");
            } catch (e) {
            }
            ;
            try {
                return new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
            }
            ;
            try {
                return new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {
            }
            ;
        };
    }
    var request = new XMLHttpRequest();
    request.open("GET", url, true);
    request.setRequestHeader("Content-Type", "application/x-javascript;");
    request.onreadystatechange = function() {
        if (request.readyState == 4 && request.status == 200) {
            if (request.responseText) {
                callbackFunction(request.responseText);
            }
        }
    };
    request.send(vars);
}













function GV_Get_Dynamic_Markers(opts) { // opts = options hash
    // this next line checks whether ALL gv_options were sent, or just the dynamic_marker_options:
    var dmo = (opts['dynamic_marker_options']) ? opts['dynamic_marker_options'] : opts;
    if (!dmo['db'] || !dmo['url']) {
        return;
    }

    var program_on_server = dmo['url'];
    var map_name = (opts['map'] && typeof(opts['map']) !== 'function') ? opts['map'] : 'gmap';
    var map = eval(map_name);
    if (!map) {
        return false;
    }
    var array_name = (opts['marker_array']) ? opts['marker_array'] : 'gv_dynamic_markers';

    if (self.gv_options && gv_options['marker_filter_options']) {
        gv_options['marker_filter_options']['filter'] = false;
    }
    gv_filter_waypoints = false;

    if (!eval('self.' + array_name)) {
        eval(array_name + ' = [];');
    }
    var marker_array = eval(array_name);

    var SW = map.getBounds().getSouthWest();
    var NE = map.getBounds().getNorthEast();
    var lat_center = map.getCenter().lat().toFixed(7);
    var lon_center = map.getCenter().lng().toFixed(7);

    var moved_enough = true;
    if (self.gv_zoom_when_reloaded && (map.getZoom() < gv_zoom_when_reloaded)) {
        moved_enough = true; // regardless of how far they moved, reload if they zoomed out
    } else if (self.gv_center_when_reloaded && self.gv_last_radius) {
        if (map.getCenter().distanceFrom(gv_center_when_reloaded) < gv_last_radius * 0.4) {
            moved_enough = false; // they moved, but not very far, so don't reload
        }
    }
    if (moved_enough) {
        for (var i = 0; i < marker_array.length; i++) {
            map.removeOverlay(marker_array[i]);
        }
        gv_marker_count = 0;
        marker_array.length = 0; // this is important!
        if (gv_marker_list_exists) {
            gv_marker_list_div.innerHTML = 'Loading markers...';
            gv_marker_list_html = '';
            gv_marker_list_folders = [];
        }
        var fields = (dmo['fields']) ? dmo['fields'] : 'name,description,latitude,longitude';
        var sort = (dmo['sort']) ? dmo['sort'] : '';
        var url = program_on_server + '?db=' + dmo['db'] + '&fields=' + escape(fields) + '&lat_center=' + lat_center + '&lon_center=' + lon_center + '&lat_min=' + SW.lat().toFixed(7) + '&lat_max=' + NE.lat().toFixed(7) + '&lon_min=' + SW.lng().toFixed(7) + '&lon_max=' + NE.lng().toFixed(7) + '&quota=' + dmo['limit'] + '&sort=' + sort;
        GDownloadUrl(url, function(data, responseCode) {
            var xml = GXml.parse(data);
            var marker_tags = xml.documentElement.getElementsByTagName("marker");
            for (var i = 0; i < marker_tags.length; i++) {
                var this_color = (marker_tags[i].getAttribute("color")) ? marker_tags[i].getAttribute("color") : '';
                var this_icon = (marker_tags[i].getAttribute("icon")) ? marker_tags[i].getAttribute("icon") : '';
                var this_label = (marker_tags[i].getAttribute("label")) ? marker_tags[i].getAttribute("label") : marker_tags[i].getAttribute("name");
                var this_shortdesc = (marker_tags[i].getAttribute("shortdesc")) ? marker_tags[i].getAttribute("shortdesc") : '';
                var this_folder = (marker_tags[i].getAttribute("folder")) ? marker_tags[i].getAttribute("folder") : '';
                this_label = (dmo['labels']) ? this_label : '';
                var m = GV_Marker(map, {
                    lat: marker_tags[i].getAttribute("lat"),
                    lon: marker_tags[i].getAttribute("lon"),
                    name: marker_tags[i].getAttribute("name"),
                    desc: marker_tags[i].getAttribute("desc"),
                    shortdesc: this_shortdesc,
                    color: this_color,
                    icon: this_icon,
                    label: this_label,
                    folder: this_folder
                });
                marker_array.push(m);
            }
            gv_last_radius = 0.7 * map.getCenter().distanceFrom(map.getBounds().getSouthWest()); // in case the XML doesn't have a radius
            if (dmo['circle']) {
                var trackpoints = xml.documentElement.getElementsByTagName("trkpt");
                if (trackpoints.length) {
                    var tracks = xml.documentElement.getElementsByTagName("trk");
                    gv_last_radius = tracks[0].getAttribute("radius");
                    var pts = [];
                    for (var i = 0; i < trackpoints.length; i++) {
                        pts.push(new GLatLng(trackpoints[i].getAttribute("lat"), trackpoints[i].getAttribute("lon")));
                    }
                    var circle_color = (gv_options['default_marker']['color']) ? gv_options['default_marker']['color'].toLowerCase() : 'white';
                    marker_array.push(new GPolyline(pts, GV_Color_Name2Hex(circle_color), 2, 0.2));
                    map.addOverlay(marker_array[marker_array.length - 1]);
                }
            }
            if (gv_marker_list_exists) {
                GV_Marker_List();
            }
        });
        gv_center_when_reloaded = map.getCenter();
        gv_zoom_when_reloaded = map.getZoom();

    }
}


// from http://www.robertnyman.com/2006/04/24/get-the-rendered-style-of-an-element/
function getStyle(oElm, strCssRule) {
    var strValue = "";
    if (document.defaultView && document.defaultView.getComputedStyle) {
        strValue = document.defaultView.getComputedStyle(oElm, "").getPropertyValue(strCssRule);
    }
    else if (oElm.currentStyle) {
        try {
            strCssRule = strCssRule.replace(/\-(\w)/g, function(strMatch, p1) {
                return p1.toUpperCase();
            });
            strValue = oElm.currentStyle[strCssRule];
        }
        catch (e) {
            // Used to prevent an error in IE 5.0
        }
    }
    return strValue;
}



