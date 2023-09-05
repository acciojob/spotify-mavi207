package com.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        // Initialize all the hashmaps and lists
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist = findArtistByName(artistName);
        if (artist == null) {
            artist = createArtist(artistName);
        }
        Album album = new Album(title);
        albums.add(album);
        // Add the album to the artist's collection
        artistAlbumMap.computeIfAbsent(artist, k -> new ArrayList<>()).add(album);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album album = findAlbumByName(albumName);
        if (album == null) {
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title, length);
        songs.add(song);
        // Add the song to the album's collection
        albumSongMap.computeIfAbsent(album, k -> new ArrayList<>()).add(song);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user = findUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        // Add songs with the given length to the playlist
        List<Song> songsToAdd = new ArrayList<>();
        for (Song song : songs) {
            if (song.getLength() == length) {
                songsToAdd.add(song);
            }
        }
        playlistSongMap.put(playlist, songsToAdd);
        // Set the playlist creator and listener
        creatorPlaylistMap.put(user, playlist);
        userPlaylistMap.computeIfAbsent(user, k -> new ArrayList<>()).add(playlist);
        playlistListenerMap.computeIfAbsent(playlist, k -> new ArrayList<>()).add(user);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = findUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);
        // Add songs with the given titles to the playlist
        List<Song> songsToAdd = new ArrayList<>();
        for (String songTitle : songTitles) {
            Song song = findSongByTitle(songTitle);
            if (song != null) {
                songsToAdd.add(song);
            }
        }
        playlistSongMap.put(playlist, songsToAdd);
        // Set the playlist creator and listener
        creatorPlaylistMap.put(user, playlist);
        userPlaylistMap.computeIfAbsent(user, k -> new ArrayList<>()).add(playlist);
        playlistListenerMap.computeIfAbsent(playlist, k -> new ArrayList<>()).add(user);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user = findUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(playlistTitle)) {
                // Add the user as a listener
                playlistListenerMap.computeIfAbsent(playlist, k -> new ArrayList<>()).add(user);
                return playlist;
            }
        }
        throw new Exception("Playlist does not exist");
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = findUserByMobile(mobile);
        if (user == null) {
            throw new Exception("User does not exist");
        }
        Song song = findSongByTitle(songTitle);
        if (song == null) {
            throw new Exception("Song does not exist");
        }
        // Check if the user has already liked the song
        if (!songLikeMap.computeIfAbsent(song, k -> new ArrayList<>()).contains(user)) {
            songLikeMap.computeIfAbsent(song, k -> new ArrayList<>()).add(user);
            // Auto-like the corresponding artist
            Artist artist = findArtistByAlbum(findAlbumBySong(song));
            if (artist != null) {
                artist.setLikes(artist.getLikes() + 1);
            }
        }
        return song;
    }

    public String mostPopularArtist() {
        Artist mostPopularArtist = null;
        int maxLikes = -1;
        for (Artist artist : artists) {
            if (artist.getLikes() > maxLikes) {
                maxLikes = artist.getLikes();
                mostPopularArtist = artist;
            }
        }
        return (mostPopularArtist != null) ? mostPopularArtist.getName() : "";
    }

    public String mostPopularSong() {
        Song mostPopularSong = null;
        int maxLikes = -1;
        for (Song song : songs) {
            int numLikes = songLikeMap.getOrDefault(song, new ArrayList<>()).size();
            if (numLikes > maxLikes) {
                maxLikes = numLikes;
                mostPopularSong = song;
            }
        }
        return (mostPopularSong != null) ? mostPopularSong.getTitle() : "";
    }

    // Helper methods to find objects by name
    private User findUserByMobile(String mobile) {
        for (User user : users) {
            if (user.getMobile().equals(mobile)) {
                return user;
            }
        }
        return null;
    }

    private Artist findArtistByName(String name) {
        for (Artist artist : artists) {
            if (artist.getName().equals(name)) {
                return artist;
            }
        }
        return null;
    }

    private Album findAlbumByName(String title) {
        for (Album album : albums) {
            if (album.getTitle().equals(title)) {
                return album;
            }
        }
        return null;
    }

    private Song findSongByTitle(String title) {
        for (Song song : songs) {
            if (song.getTitle().equals(title)) {
                return song;
            }
        }
        return null;
    }

    private Album findAlbumBySong(Song song) {
        for (Album album : albums) {
            if (albumSongMap.getOrDefault(album, new ArrayList<>()).contains(song)) {
                return album;
            }
        }
        return null;
    }

    private Artist findArtistByAlbum(Album album) {
        for (Map.Entry<Artist, List<Album>> entry : artistAlbumMap.entrySet()) {
            if (entry.getValue().contains(album)) {
                return entry.getKey();
            }
        }
        return null;
    }



}
