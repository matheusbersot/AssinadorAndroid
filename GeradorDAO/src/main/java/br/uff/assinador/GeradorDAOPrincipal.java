package br.uff.assinador;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class GeradorDAOPrincipal {

	public static void main(String[] args) throws IOException, Exception {
		Schema schema = new Schema(1000, "br.uff.assinador");

		adicionaUsuarioDocumento(schema);

		new DaoGenerator().generateAll(schema, "./src-gen");
	}
	
    private static void adicionaUsuarioDocumento(Schema schema) {
        
    	// Adicionando entidade usuário
        Entity usuario = schema.addEntity("Usuario");
        usuario.addIdProperty();
        usuario.addStringProperty("cpf").notNull();    
    	
    	// Adicionando entidade documento
    	Entity documento = schema.addEntity("Documento");
        documento.addIdProperty();
        documento.addStringProperty("nome").notNull();
        documento.addStringProperty("tipo").notNull();
        documento.addStringProperty("descricao").notNull();
        documento.addByteArrayProperty("arquivo").notNull();        
        documento.addByteArrayProperty("assinatura");
        documento.addStringProperty("tipo_assinatura");
        Property dataRecebimento = documento.addDateProperty("data").getProperty();
        
        //adicionando chave estrangeira na tabela documento
        Property usuarioId = documento.addLongProperty("usuarioId").notNull().getProperty();
        documento.addToOne(usuario, usuarioId);
        
        //adicionando lado muitos na tabela usuário
        ToMany usuariosParaDocumentos = usuario.addToMany(documento, usuarioId);
        usuariosParaDocumentos.setName("documentos");
        usuariosParaDocumentos.orderAsc(dataRecebimento);
    }
}
