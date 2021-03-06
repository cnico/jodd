// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.proxetta.asm;

import jodd.asm.MethodAdapter;
import jodd.asm5.MethodVisitor;
import jodd.asm5.Label;
import static jodd.asm5.Opcodes.*;
import jodd.proxetta.ProxettaException;

/**
 * Method adapter that tracks history of previous instructions.
 * <ul>
 * <li>that remembers previous opcode of 'insn' and 'intInsn' instructions.
  * Used to detect single (last) int argument value of a method call.</li>
 * <li>Stores last two arguments as strings</li>
 * </ul>
 */
abstract class HistoryMethodAdapter extends MethodAdapter {

	protected HistoryMethodAdapter(MethodVisitor methodVisitor) {
		super(methodVisitor);
	}

	// ---------------------------------------------------------------- history

	protected int opcode;
	protected int operand;
	protected boolean isPrevious;       // true only if previous opcode is of the correct type
	protected boolean traceNext;        // true only to trace very next opcode
	protected String[] strArgs = new String[2];

	// ---------------------------------------------------------------- get index

	/**
	 * Returns argument index from the history.
	 * <b>Must</b> POP value from the stack after the execution.
	 */
	protected int getArgumentIndex() {
		if (isPrevious == false) {
			throw new ProxettaException("Unexpected previous instruction type used for setting argument index");
		}
		int argIndex;
		switch (opcode) {
			case ICONST_0: argIndex = 0; break;
			case ICONST_1: argIndex = 1; break;
			case ICONST_2: argIndex = 2; break;
			case ICONST_3: argIndex = 3; break;
			case ICONST_4: argIndex = 4; break;
			case ICONST_5: argIndex = 5; break;
			case BIPUSH:
			case SIPUSH:
				argIndex = operand; break;
			default:
				throw new ProxettaException("Unexpected previous instruction used for setting argument index");
		}
		return argIndex;
	}

	/**
	 * Returns last two string arguments.
	 */
	public String[] getLastTwoStringArguments() {
		return strArgs;
	}

	/**
	 * Adds last LDC arguments to {@link #getLastTwoStringArguments() string arguments}.
	 */
	private void keepStringArgument(Object value) {
		strArgs[0] = strArgs[1];
		strArgs[1] = value.toString();
	}

	// ---------------------------------------------------------------- visitors

	@Override
	public void visitInsn(int opcode) {
		this.opcode = opcode;
		isPrevious = true;
		traceNext = false;
		super.visitInsn(opcode);
	}

	@Override
	public void visitIntInsn(int opcode, int operand) {
		this.opcode = opcode;
		this.operand = operand;
		isPrevious = true;
		traceNext = false;
		super.visitIntInsn(opcode, operand);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		isPrevious = false;
		traceNext = false;
		super.visitVarInsn(opcode, var);
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		isPrevious = false;
		traceNext = false;
		super.visitTypeInsn(opcode, type);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		isPrevious = false;
		traceNext = false;
		super.visitFieldInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		isPrevious = false;
		traceNext = false;
		super.visitMethodInsn(opcode, owner, name, desc);
	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {
		isPrevious = false;
		traceNext = false;
		super.visitJumpInsn(opcode, label);
	}

	@Override
	public void visitLdcInsn(Object cst) {
		isPrevious = false;
		traceNext = false;

		keepStringArgument(cst);

		super.visitLdcInsn(cst);
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		isPrevious = false;
		traceNext = false;
		super.visitIincInsn(var, increment);
	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
		isPrevious = false;
		traceNext = false;
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		isPrevious = false;
		traceNext = false;
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		isPrevious = false;
		traceNext = false;
		super.visitMultiANewArrayInsn(desc, dims);
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		//super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		//super.visitLineNumber(line, start);
	}

}