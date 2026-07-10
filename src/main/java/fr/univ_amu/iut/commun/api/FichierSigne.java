package fr.univ_amu.iut.commun.api;

/// Résultat d'une **déclaration de fichier** (`POST /fichiers`, #142) : l'identifiant créé côté VigieChiro
/// et l'**URL S3 pré-signée** vers laquelle téléverser les octets. Le flux d'upload est en trois temps :
/// déclarer le fichier ([ClientVigieChiro#creerFichier]) → **PUT** des octets vers [#urlSignee]
/// ([ClientVigieChiro#televerserVersS3]) → finaliser ([ClientVigieChiro#finaliserFichier]).
///
/// @param id identifiant du fichier créé (`_id`)
/// @param urlSignee URL S3 pré-signée (`s3_signed_url`) où déposer les octets par `PUT`
public record FichierSigne(String id, String urlSignee) {}
