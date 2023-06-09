package app.lab3albums.controller;

import app.lab3albums.dto.*;
import app.lab3albums.entity.Album;
import app.lab3albums.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/albums")
public class AlbumController {
    private AlbumService albumService;
    
    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> create(@RequestBody PostAlbumRequest postAlbumRequest, UriComponentsBuilder builder) {
        Album album = Album.builder()
                .name(postAlbumRequest.getName())
                .artist(postAlbumRequest.getArtist())
                .releaseYear(postAlbumRequest.getReleaseYear())
                .length(postAlbumRequest.getLength())
                .build();
        album = albumService.create(album);
        return ResponseEntity.created(builder.pathSegment("api", "album", "{id}")
                .buildAndExpand(album.getId()).toUri()).build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetAlbumResponse> read(@PathVariable(name = "id") int id) {
        Optional<Album> response = albumService.find(id);
        if (response.isPresent()) {
            Album album = response.get();
            return ResponseEntity.ok(GetAlbumResponse.builder()
                    .id(album.getId())
                    .name(album.getName())
                    .artist(album.getArtist())
                    .releaseYear(album.getReleaseYear())
                    .length(album.getLength())
                    .build());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetAlbumsResponse> readAll() {
        List<Album> albums = albumService.findAll();
        return ResponseEntity.ok(
                GetAlbumsResponse.builder()
                        .albums(
                                albums.stream().map(
                                        (album) -> GetAlbumResponse.builder()
                                                .id(album.getId())
                                                .name(album.getName())
                                                .artist(album.getArtist())
                                                .releaseYear(album.getReleaseYear())
                                                .length(album.getLength())
                                                .build()
                                ).collect(Collectors.toList())
                        )
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable(name = "id") int id,
                                       @RequestBody PutAlbumResponse albumResponse) {
        try {
            Album album = Album.builder()
                    .name(albumResponse.getName())
                    .artist(albumResponse.getArtist())
                    .releaseYear(albumResponse.getReleaseYear())
                    .length(albumResponse.getLength())
                    .build();
            albumService.update(id, albumResponse);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> delete(@PathVariable(name = "id") int id) {
        Optional<Album> album = albumService.find(id);
        if (album.isPresent()) {
            albumService.delete(album.get().getId());
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

