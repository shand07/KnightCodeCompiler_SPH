package output;
import org.objectweb.asm.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class Gen4 {
public static void main(String[] args) {
System.out.println("Creating Bytecode...");
ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC,"output/Program4", null, "java/lang/Object",null);
MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
mv.visitCode();
mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
mv.visitLdcInsn((String)"Enter your name: ");
mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
mv.visitInsn(Opcodes.DUP);
mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
mv.visitVarInsn(Opcodes.ASTORE,1);
mv.visitVarInsn(Opcodes.ALOAD,1);
mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
mv.visitVarInsn(Opcodes.ASTORE,2);
mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
mv.visitLdcInsn((String)"Enter repetitions: ");
mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
mv.visitInsn(Opcodes.DUP);
mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
mv.visitVarInsn(Opcodes.ASTORE,3);
mv.visitVarInsn(Opcodes.ALOAD,3);
mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
mv.visitVarInsn(Opcodes.ASTORE,4);
mv.visitVarInsn(Opcodes.ALOAD,4);
mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
mv.visitVarInsn(Opcodes.ISTORE,5);
Label loop = new Label();
Label endLoop = new Label();
mv.visitLabel(loop);
mv.visitVarInsn(Opcodes.ILOAD,5);
mv.visitJumpInsn(Opcodes.IFLE,endLoop);
mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
mv.visitVarInsn(Opcodes.ALOAD,2);
mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
mv.visitIincInsn(5, -1);
mv.visitJumpInsn(Opcodes.GOTO, loop);
mv.visitLabel(endLoop);

mv.visitInsn(Opcodes.RETURN);
mv.visitMaxs(0,0);
mv.visitEnd();
cw.visitEnd();

byte[] b = cw.toByteArray();
try{File out = new File("output/Program4.class");
FileOutputStream byteOut = new FileOutputStream(out);
byteOut.write(b);
byteOut.close();
}catch(IOException e){System.out.println(e.getMessage());}
System.out.println("Finished!");
}
}