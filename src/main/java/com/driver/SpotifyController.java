package com.driver;

import java.util.*;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spotify")
public class SpotifyController {

    //Autowire will not work in this case, no need to change this and add autowire
    SpotifyService spotifyService = new SpotifyService();

    @PostMapping("/add-user")
    public String createUser(@RequestParam(name = "name") String name, String mobile){
        //create the user with given name and number
        User user = spotifyService.createUser(name, mobile);
        return "Success";
    }

    @PostMapping("/add-artist")
    public String createArtist(@RequestParam(name = "name") String name){
        //create the artist with given name
        Artist artist = spotifyService.createArtist(name);
        return "Success";
    }

    @PostMapping("/add-album")
    public String createAlbum(@RequestParam(name = "title") String title, String artistName){
        //If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist
        Album album = spotifyService.createAlbum(title, artistName);
        return "Success";
    }

    @PostMapping("/add-song")
    public String createSong(String title, String albumName, int length) throws Exception{
        //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album
        try{
            Song song = spotifyService.createSong(title, albumName, length);
            return "Success";
        }
        catch (Exception e) {
            return "Album does not exist";
        }
    }

    @PostMapping("/add-playlist-on-length")
    public String createPlaylistOnLength(String mobile, String title, int length) throws Exception{
        //Create a playlist with given title and add all songs having the given length in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

        try{
            Playlist playlist = spotifyService.createPlaylistOnLength(mobile, title, length);
            return "Success";
        }
        catch (Exception e) {
            return "User does not exist";
        }
    }

    @PostMapping("/add-playlist-on-name")
    public String createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception{
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

        try{
            Playlist playlist = spotifyService.createPlaylistOnName(mobile, title, songTitles);
            return "Success";
        }
        catch (Exception e) {
            return "User does not exist";
        }
    }

    @PutMapping("/find-playlist")
    public String findPlaylist(String mobile, String playlistTitle) throws Exception{
        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating

        try{
            Playlist playlist = spotifyService.findPlaylist(mobile, playlistTitle);
            return "Success";
        }
        catch (Exception e) {
            return "Playlist does not exist";
        }
    }

    @PostMapping("/like-song")
    public String likeSong(String mobile, String songTitle) throws Exception {
        // Find the user by mobile number
        User user = spotifyService.findUserByMobile(mobile);

        if (user == null) {
            return "User does not exist";
        }

        try {
            // Like the song and auto-like the corresponding artist
            Song likedSong = spotifyService.likeSong(String.valueOf(user), songTitle);

            if (likedSong != null) {
                return "Success";
            } else {
                return "Song does not exist";
            }
        } catch (Exception e) {
            return e.getMessage(); // Handle the exception message
        }
    }


    @GetMapping("/popular-artist")
    public String mostPopularArtist(){
        //Return the artist name with maximum likes
        String mostPopularArtist = spotifyService.mostPopularArtist();
        return mostPopularArtist;
    }

    @GetMapping("/popular-song")
    public String mostPopularSong(){
        //return the song title with maximum likes
        String mostPopularSong = spotifyService.mostPopularSong();
        return mostPopularSong;
    }
}
